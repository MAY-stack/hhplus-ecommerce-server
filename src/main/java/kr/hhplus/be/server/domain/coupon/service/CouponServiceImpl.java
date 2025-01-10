package kr.hhplus.be.server.domain.coupon.service;


import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.exception.CouponNotFoundException;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {
    private final CouponRepository couponRepository;

    // 쿠폰 조회
    @Override
    public Coupon getCouponById(Long couponId) {
        return couponRepository.findById(couponId)
                .orElseThrow(CouponNotFoundException::new);
    }

    @Override
    @Transactional
    public Coupon decreaseRemaining(Long couponId) {
        // 락을 사용해 쿠폰 조회
        Coupon coupon = couponRepository.findCouponByIdWithLock(couponId)
                .orElseThrow(CouponNotFoundException::new);
        coupon.decreaseRemaining(); // 수량 감소
        return couponRepository.save(coupon);
    }

    @Override
    public List<Long> getAllCouponIds() {
        return couponRepository.findAll()
                .stream()
                .map(Coupon::getId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> getAllExpiredCouponIds() {
        return couponRepository.findByExpirationDateBefore(LocalDate.now())
                .stream()
                .map(Coupon::getId)
                .collect(Collectors.toList());
    }

    @Override
    public Coupon createCoupon(String title, Long discountAmount, Long minimumOrderAmount, Integer totalQuantity, LocalDate expirationDate) {
        Coupon coupon = new Coupon(title, discountAmount, minimumOrderAmount, totalQuantity, expirationDate);
        return couponRepository.save(coupon);
    }

}
