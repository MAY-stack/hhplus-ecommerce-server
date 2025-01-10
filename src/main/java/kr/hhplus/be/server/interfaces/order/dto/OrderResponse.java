package kr.hhplus.be.server.interfaces.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.application.order.dto.OrderAndPaymentResultDto;
import kr.hhplus.be.server.domain.order.entity.OrderStatus;
import kr.hhplus.be.server.domain.payment.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "주문 생성 응답 데이터 구조")
public class OrderResponse {
    @Schema(description = "주문 ID", example = "123")
    private final Long id;

    @Schema(description = "주문한 사용자 ID", example = "123")
    private final String userId;

    @Schema(description = "주문한 총 금액", example = "50000")
    private final Long finalAmount;

    @Schema(description = "주문 처리 상태", example = "CREATED")
    private final OrderStatus orderStatus;

    @Schema(description = "결제 처리 상태", example = "CREATED")
    private final PaymentStatus paymentStatus;

    public static OrderResponse fromDto(OrderAndPaymentResultDto orderResultDto) {
        return new OrderResponse(
                orderResultDto.getOrderId(),
                orderResultDto.getUserId(),
                orderResultDto.getFinalAmount(),
                orderResultDto.getOrderStatus(),
                orderResultDto.getPaymentStatus()
        );
    }
}
