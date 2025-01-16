package kr.hhplus.be.server.application.coupon.facade;

import kr.hhplus.be.server.application.coupon.dto.CouponIssuanceInfoDto;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.entity.CouponIssuance;
import kr.hhplus.be.server.domain.coupon.service.CouponIssuanceService;
import kr.hhplus.be.server.domain.coupon.service.CouponService;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CouponFacade {
    private final UserService userService;
    private final CouponService couponService;
    private final CouponIssuanceService couponIssuanceService;

    // 쿠폰 발급
    @Transactional
    public CouponIssuanceInfoDto issueCoupon(Long couponId, String userId) {
        // 사용자 조회
        User user = userService.getUserById(userId);

        // 남은 수량 감소 및 저장
        Coupon coupon = couponService.decreaseRemaining(couponId);

        // 쿠폰 발급 및 저장
        CouponIssuance couponIssuance = couponIssuanceService.issueCoupon(coupon.getId(), user.getId());

        // 발급한 쿠폰, 쿠폰 객체 -> DTO
        return CouponIssuanceInfoDto.fromEntity(couponIssuance, coupon);
    }

    // 보유 쿠폰 목록 조회
    public List<CouponIssuanceInfoDto> getIssuedCouponList(String userId) {

        List<CouponIssuance> couponIssuanceList = couponIssuanceService.getCouponIssuanceByUserId(userId);

        return couponIssuanceList.stream()
                .map(couponIssuance -> {
                    Coupon coupon = couponService.getCouponById(couponIssuance.getCouponId());
                    return CouponIssuanceInfoDto.fromEntity(couponIssuance, coupon);
                })
                .toList();
    }

    // 쿠폰 만료 처리
    public int expireUnusedCoupons() {
        // 만료 대상 쿠폰 ID 리스트를 조회
        List<Long> expiredCouponIds = couponService.getAllExpiredCouponIds();
        // 처리 건수 count
        int count = 0;
        for (Long couponId : expiredCouponIds) {
            List<CouponIssuance> expiredIssuedCoupons = couponIssuanceService.getUnusedCouponByCouponId(couponId);
            // 발급된 쿠폰들에 대해서 만료처리
            for (CouponIssuance couponIssuance : expiredIssuedCoupons) {
                couponIssuanceService.expireUnusedCoupon(couponIssuance);
                count++;
            }
        }
        return count;
    }
}
