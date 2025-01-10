package kr.hhplus.be.server.domain.coupon.service;

import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.entity.CouponIssuance;
import kr.hhplus.be.server.domain.user.entity.Users;
import org.springframework.stereotype.Service;

@Service
public interface CouponIssuanceService {
    CouponIssuance issueCoupon(Coupon coupon, Users users);

    CouponIssuance getById(String couponIssuanceId);

    CouponIssuance save(CouponIssuance couponIssuance);
}
