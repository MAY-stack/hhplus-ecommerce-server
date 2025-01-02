package kr.hhplus.be.server.interfaces.dto.point;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PointRechargeRequest {

    @NotBlank
    @Schema(description = "사용자 ID", example = "user123", required = true)
    private String userId;


    @NotBlank
    @Schema(description = "충전 요청 포인트", example = "30000", required = true)
    private Long amount;
}
