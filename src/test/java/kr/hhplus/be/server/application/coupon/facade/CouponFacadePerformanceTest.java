package kr.hhplus.be.server.application.coupon.facade;

import kr.hhplus.be.server.application.coupon.dto.CouponIssuanceInfoDto;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.repository.CouponIssuanceRepository;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.domain.user.repository.UserRepository;
import kr.hhplus.be.server.infrastructure.cache.RedisKeyPrefix;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
@SpringBootTest
class CouponFacadePerformanceTest {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CouponIssuanceRepository couponIssuanceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private CouponFacade couponFacade; // 기존 Lock 기반 서비스

    @Autowired
    private RedisCouponFacade redisCouponFacade; // Redis 기반 서비스

    private final int THREAD_COUNT = 100; // 동시 요청 수
    private final String TEST_USER_PREFIX = "user_";

    private ExecutorService executorService;

    private Coupon coupon;

    @BeforeEach
    void setUp() {
        executorService = Executors.newFixedThreadPool(THREAD_COUNT);

        for (int i = 0; i < THREAD_COUNT; i++) {
            userRepository.save(new User(TEST_USER_PREFIX + i, "user" + i));
        }
        // 테스트용 쿠폰 생성
        Coupon newCoupon = new Coupon("Test Coupon", 1000L, 5000L, THREAD_COUNT, LocalDate.now().plusDays(1));
        coupon = couponRepository.save(newCoupon);

        // 쿠폰 잔여 수량 캐싱
        String remainingKey = RedisKeyPrefix.COUPON_REMAINING_QUANTITY.format(coupon.getId());
        redisTemplate.opsForValue().set(remainingKey, String.valueOf(coupon.getRemainingQuantity()));

    }

    @AfterEach
    void cleanData() {
        couponRepository.deleteAllInBatch();
        couponIssuanceRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @Test
    @DisplayName("DB Lock 이용 쿠폰 발급 평균 실행 시간 테스트")
    void testLockBasedCouponIssuancePerformance() throws InterruptedException {
        long lockBasedTotalTime = measureExecutionTimeForLockBasedService();
        double lockBasedAvgTime = lockBasedTotalTime / (double) THREAD_COUNT;
        log.info(String.format("Lock 기반 쿠폰 발급 평균 실행 시간: %.4f ms", lockBasedAvgTime));
    }

    @Test
    @DisplayName("Redis 이용 쿠폰 발급 평균 실행 시간 테스트")
    void testRedisBasedCouponIssuancePerformance() throws InterruptedException {
        long redisBasedTotalTime = measureExecutionTimeForRedisService();
        double redisBasedAvgTime = redisBasedTotalTime / (double) THREAD_COUNT;
        log.info(String.format("Redis 기반 쿠폰 발급 평균 실행 시간: %.4f ms", redisBasedAvgTime));
    }

    private long measureExecutionTimeForLockBasedService() throws InterruptedException {
        return measureExecutionTime(couponFacade);
    }

    private long measureExecutionTimeForRedisService() throws InterruptedException {
        return measureExecutionTime(redisCouponFacade);
    }

    private long measureExecutionTime(Object facade) throws InterruptedException {
        long startTime = System.currentTimeMillis();

        List<Future<CouponIssuanceInfoDto>> futures = new ArrayList<>();

        for (int i = 0; i < THREAD_COUNT; i++) {
            final String userId = TEST_USER_PREFIX + i;
            Callable<CouponIssuanceInfoDto> task = () -> {
                if (facade instanceof CouponFacade) {
                    return ((CouponFacade) facade).issueCoupon(coupon.getId(), userId);
                } else {
                    return ((RedisCouponFacade) facade).issueCoupon(coupon.getId(), userId);
                }
            };
            futures.add(executorService.submit(task));
        }

        for (Future<CouponIssuanceInfoDto> future : futures) {
            try {
                future.get();
            } catch (ExecutionException e) {
                log.error("쿠폰 발급 중 오류 발생", e);  // 예외 발생 시 에러 로그 출력
            }
        }

        long endTime = System.currentTimeMillis();
        return endTime - startTime;
    }
}
