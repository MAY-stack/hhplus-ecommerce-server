package kr.hhplus.be.server.interfaces.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.application.order.dto.OrderAndPaymentResultDto;
import kr.hhplus.be.server.domain.order.entity.OrderStatus;
import kr.hhplus.be.server.domain.payment.entity.PaymentStatus;
import lombok.Builder;

@Builder
@Schema(description = "주문 생성 응답 데이터 구조")
public record OrderResponse(
        @Schema(description = "주문 ID", example = "123")
        Long orderId,
        @Schema(description = "주문한 사용자 ID", example = "123")
        String userId,
        @Schema(description = "주문한 총 금액", example = "50000")
        Long finalAmount,
        @Schema(description = "주문 처리 상태", example = "CREATED")
        OrderStatus orderStatus,
        @Schema(description = "결제 처리 상태", example = "CREATED")
        PaymentStatus paymentStatus
) {
    public static OrderResponse from(OrderAndPaymentResultDto orderResultDto) {
        return OrderResponse.builder()
                .orderId(orderResultDto.orderId())
                .userId(orderResultDto.userId())
                .finalAmount(orderResultDto.finalAmount())
                .orderStatus(orderResultDto.orderStatus())
                .paymentStatus(orderResultDto.paymentStatus())
                .build();
    }
}
