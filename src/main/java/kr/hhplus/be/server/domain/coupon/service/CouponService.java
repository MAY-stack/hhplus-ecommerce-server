package kr.hhplus.be.server.domain.coupon.service;

import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public interface CouponService {
    // 쿠폰 조회
    Coupon getCouponById(Long couponId);

    Coupon decreaseRemaining(Long couponId);

    // 전체 쿠폰 아이디 목록
    List<Long> getAllCouponIds();

    // 만료된 쿠폰 아이디 목록
    List<Long> getAllExpiredCouponIds();

    Coupon createCoupon(String title, Long discountAmount, Long minimumOrderAmount, Integer totalQuantity, LocalDate expirationDate);

}
