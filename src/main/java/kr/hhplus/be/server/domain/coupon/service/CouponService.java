package kr.hhplus.be.server.domain.coupon.service;


import kr.hhplus.be.server.common.exception.ErrorMessage;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CouponService {
    private final CouponRepository couponRepository;

    // 쿠폰 생성
    public Coupon createCoupon(String title, Long discountAmount, Long minimumOrderAmount, Integer totalQuantity, LocalDate expirationDate) {
        Coupon coupon = new Coupon(title, discountAmount, minimumOrderAmount, totalQuantity, expirationDate);
        return couponRepository.save(coupon);
    }

    // 쿠폰 조회
    public Coupon getCouponById(Long couponId) {
        return couponRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.COUPON_NOT_FOUND.getMessage()));
    }

    // 쿠폰 수량 감소
    @Transactional
    public Coupon decreaseRemaining(Long couponId) {
        // 락을 사용해 쿠폰 조회
        Coupon coupon = couponRepository.findCouponByIdWithLock(couponId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.COUPON_NOT_FOUND.getMessage()));
        coupon.decreaseRemaining(); // 수량 감소
        return couponRepository.save(coupon);
    }

    // 만료된 쿠폰아이디 조회(전일이 만료일인 쿠폰 아이디 조회)
    public List<Long> getAllExpiredCouponIds() {
        return couponRepository.findAllByExpirationDate(LocalDate.now().minusDays(1))
                .stream()
                .map(Coupon::getId)
                .collect(Collectors.toList());
    }

    // 쿠폰 적용
    public Long applyCoupon(Long totalAmount, Long couponId) {
        Coupon coupon = getCouponById(couponId);
        if (totalAmount < coupon.getMinimumOrderAmount()) {
            throw new IllegalArgumentException(ErrorMessage.ORDER_AMOUNT_BELOW_COUPON_MINIMUM.getMessage());
        }
        return totalAmount - coupon.getDiscountAmount();
    }

}
