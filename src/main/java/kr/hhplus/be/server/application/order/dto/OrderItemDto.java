package kr.hhplus.be.server.application.order.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class OrderItemDto {
    private final Long productId;
    private final Integer quantity;
}

