package kr.hhplus.be.server.application.order.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@Builder
@RequiredArgsConstructor
public class OrderDto {
    private final String userId;
    private final List<OrderItemDto> orderItemDtoList;
    private final String couponIssuanceId;
}
