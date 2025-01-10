package kr.hhplus.be.server.application.external.dto;

import kr.hhplus.be.server.domain.order.entity.Orders;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ExternalRequestDto {
    private String userId;
    private Long orderId;
    private Long amount;

    public ExternalRequestDto(Orders orders) {
        this.userId = orders.getUserId();
        this.orderId = orders.getId();
        this.amount = amount;
    }
}
