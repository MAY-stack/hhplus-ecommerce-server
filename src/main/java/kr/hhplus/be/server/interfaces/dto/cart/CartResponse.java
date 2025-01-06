package kr.hhplus.be.server.interfaces.dto.cart;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "장바구니 응답")
public class CartResponse {
    @Schema(description = "응답 상태", example = "success")
    private String status;

    @Schema(description = "응답 메시지", example = "장바구니에 제품이 추가되었습니다.")
    private String message;

    @Schema(description = "현재 장바구니 목록")
    private List<CartItem> cartItems;
}
