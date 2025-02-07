package kr.hhplus.be.server.domain.coupon.service;

import kr.hhplus.be.server.common.exception.ErrorMessage;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.server.infrastructure.cache.RedisKeyPrefix;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisCouponService {
    private final CouponRepository couponRepository;
    private final RedisTemplate<String, String> redisTemplate;

    // 쿠폰 생성 시 쿠폰 정보(수량) 캐싱
    public Coupon createCouponWithCache(String title, Long discountAmount, Long minimumOrderAmount, Integer totalQuantity, LocalDate expirationDate) {
        Coupon coupon = new Coupon(title, discountAmount, minimumOrderAmount, totalQuantity, expirationDate);
        Coupon savedCoupon = couponRepository.save(coupon);  // 먼저 저장하여 ID를 생성

        // 쿠폰 잔여 수량 Redis에 저장
        String remainingKey = RedisKeyPrefix.COUPON_REMAINING_QUANTITY.format(savedCoupon.getId());
        redisTemplate.opsForValue().set(remainingKey, String.valueOf(savedCoupon.getRemainingQuantity()));

        return savedCoupon;
    }

    // Lua Script 정의
    private static final DefaultRedisScript<Long> DECREASE_COUPON_SCRIPT;

    static {
        DECREASE_COUPON_SCRIPT = new DefaultRedisScript<>();
        DECREASE_COUPON_SCRIPT.setScriptText(
                "local key = KEYS[1] " +
                        "local stock = tonumber(redis.call('GET', key)) " +
                        "if stock == nil then " +
                        "    return -1 " +  // 쿠폰 없음
                        "end " +
                        "if stock > 0 then " +
                        "    redis.call('DECR', key) " +
                        "    return stock - 1 " +  // 감소 후 남은 수량 반환
                        "else " +
                        "    return 0 " +  // 잔여 수량 부족
                        "end"
        );
        DECREASE_COUPON_SCRIPT.setResultType(Long.class);
    }

    /**
     * Lua Script를 사용하여 쿠폰 잔여 수량 차감
     */
    public void decreaseRemainingWithCache(Long couponId) {
        String remainingKey = RedisKeyPrefix.COUPON_REMAINING_QUANTITY.format(couponId);

        long remaining = Long.parseLong(redisTemplate.opsForValue().get(remainingKey));

        if (remaining > 0) {
            // Lua Script 실행
            remaining = redisTemplate.execute(
                    DECREASE_COUPON_SCRIPT,
                    Collections.singletonList(remainingKey) // KEYS[1]
            );
        } else if (remaining == 0) {
            log.info("잔여 수량이 부족합니다. 쿠폰 ID={}, 현재 잔여 수량={}", couponId, remaining);
            throw new IllegalArgumentException(ErrorMessage.COUPON_SOLD_OUT.getMessage());
        }
    }

    // 쿠폰 잔여수량 차감
//    @Transactional
//    public void decreaseRemainingWithCache(Long couponId) {
//        String remainingKey = RedisKeyPrefix.COUPON_REMAINING_QUANTITY.format(couponId);
//
//        // Redis
//        // 키가 존재하는지 확인
//        Boolean exists = redisTemplate.hasKey(remainingKey);
//
//        if (exists) {
//            // 키가 존재하면 차감 수행
//            Long remaining = redisTemplate.opsForValue().decrement(remainingKey);
//            if (remaining < 0) {
//                log.info("잔여 수량이 부족합니다. 쿠폰 ID={}, 현재 잔여 수량={}", couponId, remaining);
//                throw new IllegalArgumentException(ErrorMessage.COUPON_SOLD_OUT.getMessage());
//            }
//        } else {
//            log.warn("{} 쿠폰 ID={}", ErrorMessage.COUPON_NOT_FOUND.getMessage(), couponId);
//        }
//    }

}