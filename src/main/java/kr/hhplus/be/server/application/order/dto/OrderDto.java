package kr.hhplus.be.server.application.order.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class OrderDto {
    private final String userId;
    private final List<OrderItemDto> orderItemDtoList;
    private final String couponIssuanceId;
}
