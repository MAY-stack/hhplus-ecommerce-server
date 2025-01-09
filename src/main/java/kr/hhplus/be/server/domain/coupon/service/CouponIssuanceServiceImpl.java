package kr.hhplus.be.server.domain.coupon.service;


import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.entity.CouponIssuance;
import kr.hhplus.be.server.domain.coupon.exception.CouponAlreadyIssuedException;
import kr.hhplus.be.server.domain.coupon.exception.CouponNotFoundException;
import kr.hhplus.be.server.domain.coupon.repository.CouponIssuanceRepository;
import kr.hhplus.be.server.domain.user.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponIssuanceServiceImpl implements CouponIssuanceService {

    private final CouponIssuanceRepository couponIssuanceRepository;

    // 쿠폰 발급
    @Override
    public CouponIssuance issueCoupon(Coupon coupon, Users users) {
        // 중복 발급 조회
        if (couponIssuanceRepository.existsByCouponIdAndUserId(coupon.getId(), users.getId())) {
            throw new CouponAlreadyIssuedException();
        }
        // 쿠폰 발급
        CouponIssuance couponIssuance = new CouponIssuance(coupon.getId(), users.getId());
        return couponIssuanceRepository.save(couponIssuance);
    }

    @Override
    public CouponIssuance getById(String couponIssuanceId) {
        return couponIssuanceRepository.findById(couponIssuanceId)
                .orElseThrow(CouponNotFoundException::new);
    }

    @Override
    public CouponIssuance save(CouponIssuance couponIssuance) {
        return couponIssuanceRepository.save(couponIssuance);
    }
}
