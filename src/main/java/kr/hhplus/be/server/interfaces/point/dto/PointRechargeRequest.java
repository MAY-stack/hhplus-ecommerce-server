package kr.hhplus.be.server.interfaces.point.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;

@Schema(description = "포인트 충전 요청 데이터 구조")
public record PointRechargeRequest(
        @Min(value = 1, message = "충전 포인트는 최소 1 이상이어야 합니다.")
        @Schema(description = "충전 요청 포인트", example = "500000", requiredMode = Schema.RequiredMode.REQUIRED)
        Long amount
) {
}
