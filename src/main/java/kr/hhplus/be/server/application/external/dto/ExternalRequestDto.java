package kr.hhplus.be.server.application.external.dto;

import kr.hhplus.be.server.domain.order.entity.Order;
import lombok.Builder;

@Builder
public record ExternalRequestDto(
        String userId,
        Long orderId,
        Long amount
) {
    public static ExternalRequestDto from(Order order) {
        return ExternalRequestDto.builder()
                .userId(order.getUserId())
                .orderId(order.getId())
                .amount(order.getTotalAmount())
                .build();
    }
}
