package kr.hhplus.be.server.interfaces.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "주문 상품 정보 데이터 구조")
@Getter
@Setter
@NoArgsConstructor
public class OrderProduct {

    @NotNull
    @Schema(description = "상품 ID", example = "123", required = true)
    private Long productId;

    @NotNull
    @Schema(description = "옵션 ID", example = "1", required = true, defaultValue = "1")
    private Integer optionId;

    @NotNull
    @Schema(description = "상품 수량", example = "2", required = true)
    private Integer quantity;

}