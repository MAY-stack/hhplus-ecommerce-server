package kr.hhplus.be.server;

import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.server.infrastructure.cache.RedisKeyPrefix;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@RequiredArgsConstructor
public class CouponSyncSchedulerTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private ScheduledTask scheduledTask;

    private Coupon coupon;

    @BeforeEach
    void setUp() {
        // 1️. 테스트용 쿠폰 생성 (DB)
        coupon = new Coupon("Test Coupon", 1000L, 5000L, 100, LocalDate.now().plusDays(30));
        couponRepository.save(coupon);

        // 2️. Redis에 쿠폰 데이터 저장
        String redisKey = RedisKeyPrefix.COUPON_REMAINING_QUANTITY.format(coupon.getId());
        redisTemplate.opsForValue().set(redisKey, "80"); // Redis에서 80개로 감소했다고 가정
    }

    @Test
    @DisplayName("배치 실행 후 Redis의 쿠폰 잔여수량이 DB에 반영되어야 한다.")
    void testSyncCouponRemainingQuantity() {
        // 3️. 배치 실행 전, Redis 값 확인
        String redisKey = RedisKeyPrefix.COUPON_REMAINING_QUANTITY.format(coupon.getId());
        String redisValueBefore = redisTemplate.opsForValue().get(redisKey);
        assertThat(redisValueBefore).isEqualTo("80");

        // 4. 배치 실행 (쿠폰 수량 동기화)
        scheduledTask.syncCouponRemainingQuantity();

        // 5. 배치 실행 후, DB 값 확인
        Coupon updatedCoupon = couponRepository.findById(coupon.getId()).orElseThrow();
        assertThat(updatedCoupon.getRemainingQuantity()).isEqualTo(80);

        // 6. 로그 확인
        log.info("배치 실행 완료 - DB 업데이트 확인됨");
    }
}
