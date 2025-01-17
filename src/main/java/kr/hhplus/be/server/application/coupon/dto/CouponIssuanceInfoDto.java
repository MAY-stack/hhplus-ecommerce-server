package kr.hhplus.be.server.application.coupon.dto;

import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.entity.CouponIssuance;
import kr.hhplus.be.server.domain.coupon.entity.IssuedCouponStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;


public record CouponIssuanceInfoDto(
        String id,
        String userId,
        Long couponId,
        String couponTitle,
        Long discountAmount,
        Long minimumOrderAmount,
        LocalDate expirationDate,
        LocalDateTime issuedAt,
        LocalDateTime usedAt,
        IssuedCouponStatus status
) {
    public static CouponIssuanceInfoDto from(CouponIssuance couponIssuance, Coupon coupon) {
        return new CouponIssuanceInfoDto(
                couponIssuance.getId(),
                couponIssuance.getUserId(),
                coupon.getId(),
                coupon.getTitle(),
                coupon.getDiscountAmount(),
                coupon.getMinimumOrderAmount(),
                coupon.getExpirationDate(),
                couponIssuance.getIssuedAt(),
                couponIssuance.getUsedAt(),
                couponIssuance.getStatus()
        );
    }
}