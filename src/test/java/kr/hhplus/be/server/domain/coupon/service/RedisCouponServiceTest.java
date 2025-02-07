package kr.hhplus.be.server.domain.coupon.service;

import kr.hhplus.be.server.common.exception.ErrorMessage;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.server.infrastructure.cache.RedisKeyPrefix;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Testcontainers
class RedisCouponServiceTest {

    @Autowired
    private RedisCouponService redisCouponService;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private final String title = "Test Coupon";
    private final Long discountAmount = 1000L;
    private final Long minimumOrderAmount = 5000L;
    private final Integer totalQuantity = 50;
    private final LocalDate expirationDate = LocalDate.now().plusDays(30);
    private Coupon coupon;

    @BeforeEach
    void setUp() {
        coupon = redisCouponService.createCouponWithCache(title, discountAmount, minimumOrderAmount, totalQuantity, expirationDate);
    }

    @AfterEach
    void cleanData() {
        couponRepository.deleteAllInBatch();
        redisTemplate.getConnectionFactory().getConnection().flushAll();  // Redis 데이터 초기화
    }

    @Test
    @DisplayName("쿠폰 생성 시, 쿠폰의 잔여 수량을 캐시로 저장한다")
    void testCreateCoupon_CachesRemainingQuantity() {
        // When
        Coupon createdCoupon = redisCouponService.createCouponWithCache(title, discountAmount, minimumOrderAmount, totalQuantity, expirationDate);

        // Then
        // 1. 쿠폰이 데이터베이스에 저장되었는지 확인
        Coupon savedCoupon = couponRepository.findById(createdCoupon.getId())
                .orElseThrow(() -> new AssertionError(ErrorMessage.COUPON_NOT_FOUND.getMessage()));

        assertThat(savedCoupon.getTitle()).isEqualTo(title);
        assertThat(savedCoupon.getDiscountAmount()).isEqualTo(discountAmount);
        assertThat(savedCoupon.getRemainingQuantity()).isEqualTo(totalQuantity);
        assertThat(savedCoupon.getExpirationDate()).isEqualTo(expirationDate);

        // 2. 쿠폰 수량이 Redis에 캐시되었는지 확인
        String key = RedisKeyPrefix.COUPON_REMAINING_QUANTITY.format(savedCoupon.getId());
        String cachedQuantity = redisTemplate.opsForValue().get(key);

        assertThat(cachedQuantity).isEqualTo(String.valueOf(totalQuantity));
    }

    @Test
    @DisplayName("쿠폰 수량이 0일 때 예외를 발생시킨다")
    void testDecreaseRemainingWithCache_ThrowsException_WhenSoldOut() {
        // 🔹 Redis의 잔여 수량을 0으로 설정
        String key = RedisKeyPrefix.COUPON_REMAINING_QUANTITY.format(coupon.getId());
        redisTemplate.opsForValue().set(key, "0");

        // 예외 발생 확인 (IllegalArgumentException 예상)
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                redisCouponService.decreaseRemainingWithCache(coupon.getId())
        );

        assertThat(exception.getMessage()).isEqualTo(ErrorMessage.COUPON_SOLD_OUT.getMessage());
    }
    
}
