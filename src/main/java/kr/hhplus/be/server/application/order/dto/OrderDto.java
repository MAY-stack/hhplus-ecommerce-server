package kr.hhplus.be.server.application.order.dto;

import lombok.Builder;

import java.util.List;


@Builder
public record OrderDto(
        String userId,
        List<OrderItemDto> orderItemDtoList,
        String couponIssuanceId
) {
}
