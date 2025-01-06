package kr.hhplus.be.server.interfaces.dto.cart;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "장바구니 항목")
public class CartItem {
    @Schema(description = "장바구니 항목 ID", example = "1")
    private Long id;

    @Schema(description = "카트 ID", example = "123")
    private Long cartId;

    @Schema(description = "제품 ID", example = "1001")
    private Long productId;

    @Schema(description = "옵션 ID", example = "2001")
    private Integer optionId;

    @Schema(description = "수량", example = "2")
    private Integer quantity;

    @Schema(description = "제품 단가", example = "50000")
    private Long unitPrice;

    @Schema(description = "총 금액", example = "100000")
    private Long totalPrice;

}
