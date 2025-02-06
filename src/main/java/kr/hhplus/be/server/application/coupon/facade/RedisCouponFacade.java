package kr.hhplus.be.server.application.coupon.facade;

import kr.hhplus.be.server.application.coupon.dto.CouponIssuanceInfoDto;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.entity.CouponIssuance;
import kr.hhplus.be.server.domain.coupon.service.CouponService;
import kr.hhplus.be.server.domain.coupon.service.RedisCouponIssuanceService;
import kr.hhplus.be.server.domain.coupon.service.RedisCouponService;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RedisCouponFacade {

    private final UserService userService;
    private final RedisCouponService redisCouponService;
    private final CouponService couponService;
    private final RedisCouponIssuanceService redisCouponIssuanceService;

    // 쿠폰 발급 - Redis 이용 동기처리
    @Transactional
    public CouponIssuanceInfoDto issueCoupon(Long couponId, String userId) {
        // 사용자 조회
        User user = userService.getUserById(userId);

        // 쿠폰 조회
        Coupon coupon = couponService.getCouponById(couponId);

        // 남은 수량 감소 및 저장 - Redis 이용
        redisCouponService.decreaseRemainingWithCache(couponId);

        // 쿠폰 발급 및 저장 - Redis 이용
        CouponIssuance couponIssuance = redisCouponIssuanceService.issueCoupon(couponId, user.getId());

        // 발급한 쿠폰, 쿠폰 객체 -> DTO
        return CouponIssuanceInfoDto.from(couponIssuance, coupon);
    }

}
