package kr.hhplus.be.server.interfaces.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.interfaces.dto.order.OrderRequest;
import kr.hhplus.be.server.interfaces.dto.order.OrderResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "order", description = "주문 API")
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    @Operation(summary = "주문 요청 API")
    @PostMapping
    public ResponseEntity<OrderResponse> postOrder(@RequestBody OrderRequest orderRequest) {
        OrderResponse orderResponse = new OrderResponse("success", 123L, 50000L);
        return ResponseEntity.ok(orderResponse);
    }
}
