package kr.hhplus.be.server.domain.payment.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.common.exception.ErrorMessage;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long orderId;

    @Column(nullable = false)
    private Long amount;

    @CreationTimestamp
    private LocalDateTime paidAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    public Payment(Long orderId, Long amount, PaymentStatus status) {
        if (orderId == null) {
            throw new IllegalArgumentException(ErrorMessage.ORDER_ID_REQUIRED.getMessage());
        }
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException(ErrorMessage.MINIMUM_PAYMENT_AMOUNT_VIOLATION.getMessage());
        }
        if (status == null) {
            throw new IllegalArgumentException(ErrorMessage.PAYMENT_STATUS_REQUIRED.getMessage());
        }
        this.orderId = orderId;
        this.amount = amount;
        this.status = status;
    }

    public void updatePaymentStatus(PaymentStatus status) {
        if (status == null) {
            throw new IllegalArgumentException(ErrorMessage.PAYMENT_STATUS_REQUIRED.getMessage());
        }
        this.status = status;
    }
}
