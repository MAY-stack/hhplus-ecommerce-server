package kr.hhplus.be.server.interfaces.dto.cart;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "장바구니 요청")
@Getter
@Setter
@AllArgsConstructor
public class CartRequest {
    @Schema(description = "사용자 ID", example = "user123", required = true)
    private String userId;

    @Schema(description = "상품 ID", example = "1", required = true)
    private Long productId;

    @Schema(description = "옵션 ID", example = "101", required = true)
    private Integer optionId;

    @Schema(description = "수량", example = "2", required = true)
    private Integer quantity;
}
