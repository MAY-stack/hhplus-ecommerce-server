package kr.hhplus.be.server.interfaces.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.application.order.facade.OrderFacade;
import kr.hhplus.be.server.interfaces.order.dto.OrderRequest;
import kr.hhplus.be.server.interfaces.order.dto.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "order", description = "주문 API")
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderFacade orderFacade;

    @Operation(summary = "주문 요청 API")
    @PostMapping
    public ResponseEntity<OrderResponse> postOrder(@RequestBody OrderRequest orderRequest) {
        OrderResponse orderResponse = OrderResponse.fromDto(orderFacade.makeOrder(orderRequest.toOrderDto()));
        return ResponseEntity.ok(orderResponse);
    }
}
