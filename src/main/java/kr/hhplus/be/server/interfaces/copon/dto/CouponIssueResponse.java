package kr.hhplus.be.server.interfaces.copon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.application.coupon.dto.CouponIssuanceInfoDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "쿠폰 발급 성공 응답 데이터 구조")
public class CouponIssueResponse {
    @Schema(description = "요청 처리 상태", example = "success")
    private String status;

    @Schema(description = "쿠폰 ID", example = "789")
    private Long couponId;

    @Schema(description = "사용자 ID", example = "user123")
    private String userId;

    @Schema(description = "발급된 쿠폰의 고유 ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private String couponIssuanceId;

    public static CouponIssueResponse fromCouponIssuanceInfoDto(CouponIssuanceInfoDto couponIssuanceInfoDto) {
        return new CouponIssueResponse(
                couponIssuanceInfoDto.getStatus().toString(),
                couponIssuanceInfoDto.getCouponId(),
                couponIssuanceInfoDto.getUserId(),
                couponIssuanceInfoDto.getId()
        );
    }
}
