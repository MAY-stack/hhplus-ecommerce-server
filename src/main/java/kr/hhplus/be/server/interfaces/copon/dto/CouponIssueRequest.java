package kr.hhplus.be.server.interfaces.copon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "쿠폰 발급 요청 데이터 구조")
public class CouponIssueRequest {
    @NotNull
    @Schema(description = "쿠폰 ID", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long couponId;
}
