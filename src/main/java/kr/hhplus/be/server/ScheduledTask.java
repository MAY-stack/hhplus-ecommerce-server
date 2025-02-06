package kr.hhplus.be.server;

import kr.hhplus.be.server.application.coupon.facade.CouponFacade;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.server.infrastructure.cache.RedisKeyPrefix;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledTask {
    private final CouponFacade couponFacade;

    /**
     * 사용하지 않은 쿠폰들을 만료시키는 스케줄러 태스크
     * 매일 자정에 실행
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void expireUnusedCoupons() {
        try {
            int expireCouponCount = couponFacade.expireUnusedCoupons();
            // 로그로 실행 결과 기록
            log.info("쿠폰 만료 작업 완료: " + expireCouponCount + "건 처리됨");
        } catch (Exception e) {
            // 예외 발생 시 로그 처리
            log.info("쿠폰 만료 작업 중 오류 발생: " + e.getMessage());
        }

    }

    private final RedisTemplate<String, String> redisTemplate;
    private final CouponRepository couponRepository;

    private static final String COUPON_REMAINING_PATTERN = RedisKeyPrefix.COUPON_REMAINING_QUANTITY.getPrefix() + ":*";

    @Scheduled(fixedDelay = 10000) // 10초마다 동기화
    @Transactional
    public void syncCouponRemainingQuantity() {
        Set<String> keys = redisTemplate.keys(COUPON_REMAINING_PATTERN);
        if (keys.isEmpty()) {
            return;
        }

        for (String key : keys) {
            Long couponId = extractCouponId(key);
            String remainingStr = redisTemplate.opsForValue().get(key);

            int remaining = Integer.parseInt(remainingStr);

            // DB에 업데이트 수행
            int updatedRows = couponRepository.updateRemainingQuantity(couponId, remaining);
            if (updatedRows > 0) {
                log.info("쿠폰 ID={} 수량 동기화 완료. 현재 Redis 잔여 수량={}", couponId, remaining);
            } else {
                log.warn("쿠폰 ID={} 동기화 실패. 현재 Redis 잔여 수량={}", couponId, remaining);
            }
        }
    }

    private Long extractCouponId(String key) {
        return Long.parseLong(key.replace(RedisKeyPrefix.COUPON_REMAINING_QUANTITY.getPrefix() + ":", ""));
    }
}
