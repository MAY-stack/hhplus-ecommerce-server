package kr.hhplus.be.server.domain.coupon.service;

import kr.hhplus.be.server.common.exception.ErrorMessage;
import kr.hhplus.be.server.domain.coupon.entity.CouponIssuance;
import kr.hhplus.be.server.domain.coupon.repository.CouponIssuanceRepository;
import kr.hhplus.be.server.infrastructure.cache.RedisKeyPrefix;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisCouponIssuanceService {
    private final CouponIssuanceRepository couponIssuanceRepository;
    private final RedisTemplate<String, String> redisTemplate;

    // 쿠폰 발급
    public CouponIssuance issueCoupon(Long couponId, String userId) {
        String key = RedisKeyPrefix.COUPON_ISSUED_USER.format(couponId);

        // Redis에서 중복 발급 체크
        if (redisTemplate.opsForSet().isMember(key, userId)) {
            throw new IllegalArgumentException(ErrorMessage.COUPON_ALREADY_ISSUED.getMessage());
        }
        // 쿠폰 발급 및 DB 저장
        CouponIssuance couponIssuance = new CouponIssuance(couponId, userId);
        CouponIssuance savedCouponIssuance;

        try {
            savedCouponIssuance = couponIssuanceRepository.save(couponIssuance);
        } catch (DataIntegrityViolationException e) {
            // DB Unique 제약 조건 위반 시 중복 예외 처리
            throw new IllegalArgumentException(ErrorMessage.COUPON_ALREADY_ISSUED.getMessage(), e);
        }

        try {
            // Redis에 유저 추가 (DB 저장 성공 후)
            redisTemplate.opsForSet().add(key, userId);
        } catch (Exception e) {
            // Redis 저장 실패 시 경고 로그 남기기
            log.warn("Redis 저장 실패: 쿠폰ID={}, 유저ID={}", couponId, userId, e);
        }

        return savedCouponIssuance;
    }
}
