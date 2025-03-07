package kr.hhplus.be.server.interfaces.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "주문 상품 정보 데이터 구조")
public record OrderItem(
        @NotNull
        @Schema(description = "상품 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        Long productId,

        @Min(value = 1, message = "최소 주문 수량은 1개입니다.")
        @Schema(description = "상품 수량", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
        Integer quantity
) {
}