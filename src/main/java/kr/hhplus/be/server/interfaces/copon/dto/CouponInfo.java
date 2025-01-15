package kr.hhplus.be.server.interfaces.copon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.application.coupon.dto.CouponIssuanceInfoDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "쿠폰 응답 데이터 구조")
@Getter
@Setter
@AllArgsConstructor
public class CouponInfo {

    @Schema(description = "쿠폰 발급 ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private String issueId;

    @Schema(description = "쿠폰 ID", example = "789")
    private Long couponId;

    @Schema(description = "쿠폰 제목", example = "1000 Discount")
    private String title;

    @Schema(description = "할인 금액", example = "1000")
    private Long discountAmount;

    @Schema(description = "최소 주문 금액", example = "5000")
    private Long minimumOrderAmount;

    @Schema(description = "쿠폰 만료일", example = "2025-01-31")
    private String expirationDate;

    @Schema(description = "쿠폰 상태", example = "issued")
    private String status;

    public static CouponInfo fromCouponIssuanceInfoDto(CouponIssuanceInfoDto couponIssuanceInfoDto) {
        return new CouponInfo(
                couponIssuanceInfoDto.getId(),
                couponIssuanceInfoDto.getCouponId(),
                couponIssuanceInfoDto.getCouponTitle(),
                couponIssuanceInfoDto.getDiscountAmount(),
                couponIssuanceInfoDto.getMinimumOrderAmount(),
                couponIssuanceInfoDto.getExpirationDate().toString(),
                couponIssuanceInfoDto.getStatus().name()
        );
    }
}
