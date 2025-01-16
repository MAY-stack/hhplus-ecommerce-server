package kr.hhplus.be.server.application.order.dto;

import lombok.Builder;

@Builder
public record OrderItemDto(
        Long productId,
        Integer quantity
) {
}

