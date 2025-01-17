package kr.hhplus.be.server.interfaces.copon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.application.coupon.dto.CouponIssuanceInfoDto;
import lombok.Builder;

@Builder
@Schema(description = "쿠폰 응답 데이터 구조")
public record CouponInfoResponse(

        @Schema(description = "쿠폰 발급 ID", example = "550e8400-e29b-41d4-a716-446655440000")
        String issueId,

        @Schema(description = "쿠폰 ID", example = "789")
        Long couponId,

        @Schema(description = "쿠폰 제목", example = "1000 Discount")
        String title,

        @Schema(description = "할인 금액", example = "1000")
        Long discountAmount,

        @Schema(description = "최소 주문 금액", example = "5000")
        Long minimumOrderAmount,

        @Schema(description = "쿠폰 만료일", example = "2025-01-31")
        String expirationDate,

        @Schema(description = "쿠폰 상태", example = "issued")
        String status
) {
    public static CouponInfoResponse from(CouponIssuanceInfoDto couponIssuanceInfoDto) {
        return CouponInfoResponse.builder()
                .issueId(couponIssuanceInfoDto.id())
                .couponId(couponIssuanceInfoDto.couponId())
                .title(couponIssuanceInfoDto.couponTitle())
                .discountAmount(couponIssuanceInfoDto.discountAmount())
                .minimumOrderAmount(couponIssuanceInfoDto.minimumOrderAmount())
                .expirationDate(couponIssuanceInfoDto.expirationDate().toString())
                .status(couponIssuanceInfoDto.status().name())
                .build();
    }
}
