package kr.hhplus.be.server.domain.coupon.service;


import kr.hhplus.be.server.common.exception.ErrorMessage;
import kr.hhplus.be.server.domain.coupon.entity.CouponIssuance;
import kr.hhplus.be.server.domain.coupon.entity.IssuedCouponStatus;
import kr.hhplus.be.server.domain.coupon.repository.CouponIssuanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponIssuanceService {

    private final CouponIssuanceRepository couponIssuanceRepository;

    // 쿠폰 발급
    public CouponIssuance issueCoupon(Long couponId, String userId) {
        // 중복 발급 조회
        if (couponIssuanceRepository.existsByCouponIdAndUserId(couponId, userId)) {
            throw new IllegalArgumentException(ErrorMessage.COUPON_ALREADY_ISSUED.getMessage());
        }
        // 쿠폰 발급
        CouponIssuance couponIssuance = new CouponIssuance(couponId, userId);
        return couponIssuanceRepository.save(couponIssuance);
    }

    // 발급 쿠폰 조회
    public CouponIssuance getCouponIssuanceById(String couponIssuanceId) {
        return couponIssuanceRepository.findById(couponIssuanceId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.COUPON_NOT_FOUND.getMessage()));
    }

    // 쿠폰 사용
    public CouponIssuance useIssuedCoupon(CouponIssuance couponIssuance, String userId) {
        couponIssuance.changeStatusToUsed(userId);
        return couponIssuanceRepository.save(couponIssuance);
    }

    // couponId에 해당하는 쿠폰 중 사용하지 않은 쿠폰 조회
    public List<CouponIssuance> getUnusedCouponByCouponId(Long couponId) {
        return couponIssuanceRepository.findByCouponIdAndStatus(couponId, IssuedCouponStatus.ISSUED);
    }

    public List<CouponIssuance> getCouponIssuanceByUserId(String userId) {
        return couponIssuanceRepository.findAllByUserId(userId);
    }

}
