package kr.hhplus.be.server.application.external.dto;

import kr.hhplus.be.server.domain.order.entity.Order;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ExternalRequestDto {
    private String userId;
    private Long orderId;
    private Long amount;

    public ExternalRequestDto(Order order) {
        this.userId = order.getUserId();
        this.orderId = order.getId();
        this.amount = order.getFinalAmount();
    }
}
