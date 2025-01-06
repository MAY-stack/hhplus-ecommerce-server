package kr.hhplus.be.server.interfaces.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.interfaces.dto.cart.CartItem;
import kr.hhplus.be.server.interfaces.dto.cart.CartRequest;
import kr.hhplus.be.server.interfaces.dto.cart.CartResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "cart", description = "장바구니 API")
@RestController
@RequestMapping("/api/v1/cart")
public class CartController {
    @Operation(summary = "장바구니 상품 추가 API", description = "사용자의 장바구니에 상품을 추가합니다.")
    @PostMapping
    public ResponseEntity<CartResponse> addToCart(@RequestBody CartRequest request) {
        // Mock 처리: 장바구니에 상품 추가
        CartItem oldCartItem
                = new CartItem(1L,
                123L,
                2001L,
                1,
                2,
                3000L,
                2 * 3000L);

        CartItem newCartItem
                = new CartItem(1L,
                123L,
                request.getProductId(),
                request.getOptionId(),
                request.getQuantity(),
                2000L,
                request.getQuantity() * 2000L);

        // 성공 응답 반환
        CartResponse response = new CartResponse(
                "success",
                "장바구니에 상품을 추가했습니다.",
                List.of(oldCartItem, newCartItem)
        );
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "장바구니 상품 삭제 API", description = "사용자의 장바구니에 상품을 삭제합니다.")
    @DeleteMapping
    public ResponseEntity<CartResponse> deleteFromCart(@RequestBody CartRequest request) {
        // Mock 처리: 장바구니에 상품 삭제한 CartItem
        CartItem oldCartItem
                = new CartItem(1L,
                123L,
                2001L,
                1,
                2,
                3000L,
                2 * 3000L);

        CartItem newCartItem
                = new CartItem(1L,
                123L,
                request.getProductId(),
                request.getOptionId(),
                request.getQuantity(),
                2000L,
                request.getQuantity() * 2000L);

        // 성공 응답 반환
        CartResponse response = new CartResponse(
                "success",
                "장바구니에서 상품을 삭제했습니다.",
                List.of(oldCartItem, newCartItem)
        );
        return ResponseEntity.ok(response);
    }
}
