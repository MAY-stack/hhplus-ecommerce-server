package kr.hhplus.be.server.application.order.dto;

import kr.hhplus.be.server.domain.order.entity.OrderStatus;
import kr.hhplus.be.server.domain.order.entity.Orders;
import kr.hhplus.be.server.domain.payment.entity.Payment;
import kr.hhplus.be.server.domain.payment.entity.PaymentStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class OrderAndPaymentResultDto {
    private final Long orderId;
    private final String userId;
    private final Long totalAmount;
    private final Long discountAmount;
    private final Long finalAmount;
    private final String couponIssuanceId;
    private final OrderStatus orderStatus;
    private final LocalDateTime paidAt;
    private final PaymentStatus paymentStatus;

    public static OrderAndPaymentResultDto fromEntity(Orders order, Payment payment) {
        return new OrderAndPaymentResultDto(
                order.getId(),
                order.getUserId(),
                order.getTotalAmount(),
                order.getDiscountAmount(),
                order.getFinalAmount(),
                order.getCouponIssuanceId(),
                order.getStatus(),
                payment.getPaidAt(),
                payment.getStatus()
        );
    }
}
