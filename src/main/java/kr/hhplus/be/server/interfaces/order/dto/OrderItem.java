package kr.hhplus.be.server.interfaces.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "주문 상품 정보 데이터 구조")
@Getter
@Setter
@NoArgsConstructor
public class OrderItem {

    @NotNull
    @Schema(description = "상품 ID", example = "123", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long productId;

    @NotNull
    @Schema(description = "상품 수량", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer quantity;
}