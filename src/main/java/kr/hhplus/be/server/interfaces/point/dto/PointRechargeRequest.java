package kr.hhplus.be.server.interfaces.point.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PointRechargeRequest {
    @Min(value = 1, message = "충전 포인트는 최소 1 이상이어야 합니다.")
    @Schema(description = "충전 요청 포인트", example = "30000", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long amount;
}
