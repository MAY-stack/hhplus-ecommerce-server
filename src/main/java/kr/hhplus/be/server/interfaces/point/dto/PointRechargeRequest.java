package kr.hhplus.be.server.interfaces.point.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PointRechargeRequest {

    @NotBlank(message = "사용자 ID는 필수입니다.")
    @Schema(description = "사용자 ID", example = "user123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String userId;


    @Min(value = 1, message = "충전 포인트는 최소 1 이상이어야 합니다.")
    @Schema(description = "충전 요청 포인트", example = "30000", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long amount;
}
