package kr.hhplus.be.server.interfaces.copon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "쿠폰 발급 요청 데이터 구조")
public class CouponIssueRequest {

    @NotBlank(message = "사용자 아이디는 필수입니다.")
    @Schema(description = "사용자 ID", example = "user123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String userId;

    @Schema(description = "쿠폰 ID", example = "789", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long couponId;
}
