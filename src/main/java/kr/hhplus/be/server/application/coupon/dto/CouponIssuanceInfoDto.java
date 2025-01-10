package kr.hhplus.be.server.application.coupon.dto;

import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.entity.CouponIssuance;
import kr.hhplus.be.server.domain.coupon.entity.IssuedCouponStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CouponIssuanceInfoDto {
    private String id;
    private String userId;
    private Long couponId;
    private String couponTitle;
    private Long discountAmount;
    private Long minimumOrderAmount;
    private LocalDate expirationDate;
    private LocalDateTime issuedAt;
    private LocalDateTime usedAt;
    private IssuedCouponStatus status;

    public static CouponIssuanceInfoDto fromEntity(CouponIssuance couponIssuance, Coupon coupon) {
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