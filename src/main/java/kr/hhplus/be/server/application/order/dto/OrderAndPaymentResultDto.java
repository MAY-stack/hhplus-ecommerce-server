package kr.hhplus.be.server.application.order.dto;

import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.entity.OrderStatus;
import kr.hhplus.be.server.domain.payment.entity.Payment;
import kr.hhplus.be.server.domain.payment.entity.PaymentStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record OrderAndPaymentResultDto(
        Long orderId,
        String userId,
        Long totalAmount,
        Long discountAmount,
        Long finalAmount,
        String couponIssuanceId,
        OrderStatus orderStatus,
        LocalDateTime paidAt,
        PaymentStatus paymentStatus
) {

    public static OrderAndPaymentResultDto from(Order order, Payment payment) {
        return OrderAndPaymentResultDto.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .totalAmount(order.getTotalAmount())
                .discountAmount(order.getDiscountAmount())
                .finalAmount(order.getFinalAmount())
                .couponIssuanceId(order.getCouponIssuanceId())
                .orderStatus(order.getStatus())
                .paidAt(payment.getPaidAt())
                .paymentStatus(payment.getStatus())
                .build();
    }
}
