package kr.hhplus.be.server.application.coupon.facade;

import kr.hhplus.be.server.ServerApplication;
import kr.hhplus.be.server.application.coupon.dto.CouponIssuanceInfoDto;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.repository.CouponIssuanceRepository;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.server.domain.coupon.service.CouponService;
import kr.hhplus.be.server.domain.user.repository.UserRepository;
import kr.hhplus.be.server.domain.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest(classes = ServerApplication.class)
@ExtendWith(MockitoExtension.class)
class CouponFacadeConcurrencyTest {

    @Autowired
    private CouponFacade couponFacade;

    @Autowired
    private CouponService couponService;

    @Autowired
    private UserService userService;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CouponIssuanceRepository couponIssuanceRepository;

    @Autowired
    private UserRepository userRepository;

    private Long couponId;
    private static final int THREAD_COUNT = 10;  // 10명의 사용자가 동시에 요청

    @BeforeEach
    void setUp() {
        // 1. 테스트용 쿠폰 생성 (1개만 발급 가능)
        Coupon coupon = couponService.createCoupon(
                "테스트 쿠폰",
                5000L,
                10000L,
                1,
                LocalDate.now().plusDays(10)
        );
        this.couponId = coupon.getId();

        // 2. 10명의 테스트 사용자 생성
        IntStream.rangeClosed(1, THREAD_COUNT).forEach(i -> {
            userService.createUser("testUser" + i, "테스트 유저" + i);
        });
    }

    @AfterEach
    void cleanData() {
        couponRepository.deleteAllInBatch();
        couponIssuanceRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    void 쿠폰_발급_동시성_테스트() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        List<Future<String>> results = new ArrayList<>();

        // 10명의 사용자가 동시에 요청을 보냄
        for (int i = 1; i <= THREAD_COUNT; i++) {
            final String userId = "testUser" + i;
            Future<String> future = executorService.submit(() -> {
                try {
                    CouponIssuanceInfoDto issuedCoupon = couponFacade.issueCoupon(couponId, userId);
                    return "SUCCESS: " + issuedCoupon.userId();
                } catch (Exception e) {
                    return "FAILED: " + userId;
                }
            });
            results.add(future);
        }

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        // ✅ 테스트 검증
        long successCount = results.stream().filter(future -> {
            try {
                return future.get().startsWith("SUCCESS");
            } catch (Exception e) {
                return false;
            }
        }).count();

        long failCount = THREAD_COUNT - successCount;

        // ❗ 성공한 요청은 단 1건이어야 함
        assertThat(successCount).isEqualTo(1);

        // ❗ 실패한 요청은 나머지 9건이어야 함
        assertThat(failCount).isEqualTo(THREAD_COUNT - 1);

        // 로그 출력
        results.forEach(result -> {
            try {
                log.info(result.get());
            } catch (Exception ignored) {
            }
        });
    }
}
