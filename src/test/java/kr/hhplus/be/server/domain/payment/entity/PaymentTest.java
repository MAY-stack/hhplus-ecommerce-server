package kr.hhplus.be.server.domain.payment.entity;

import kr.hhplus.be.server.common.exception.ErrorMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentTest {

    @Test
    @DisplayName("주문 아이디와 주문 금액, 결제 상태로 Payment 객체를 생성할 수 있다")
    void constructor_ShouldInitializeFieldsCorrectly() {
        // Arrange
        Long orderId = 1L;
        Long amount = 5000L;
        PaymentStatus status = PaymentStatus.COMPLETED;

        // Act
        Payment payment = new Payment(orderId, amount, status);

        // Assert
        assertThat(payment.getOrderId()).isEqualTo(orderId);
        assertThat(payment.getAmount()).isEqualTo(amount);
        assertThat(payment.getStatus()).isEqualTo(status);
        assertThat(payment.getPaidAt()).isNull(); // @CreationTimestamp는 저장 시점에 설정됨
        assertThat(payment.getUpdatedAt()).isNull(); // @UpdateTimestamp도 저장 시점에 설정됨
    }

    @Test
    @DisplayName("주문 아이디가 null 이면 IllegalArgumentException을 던진다")
    void constructor_ShouldThrowException_WhenOrderIdIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> new Payment(null, 5000L, PaymentStatus.PENDING))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.ORDER_ID_REQUIRED.getMessage());
    }

    @Test
    @DisplayName("주문 금액이 null 이면 IllegalArgumentException을 던진다")
    void constructor_ShouldThrowException_WhenAmountIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> new Payment(1L, null, PaymentStatus.PENDING))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.MINIMUM_PAYMENT_AMOUNT_VIOLATION.getMessage());
    }

    @Test
    @DisplayName("주문 금액이 0이거나 음수이면 IllegalArgumentException을 던진다")
    void constructor_ShouldThrowException_WhenAmountIsZeroOrNegative() {
        // Act & Assert
        assertThatThrownBy(() -> new Payment(1L, 0L, PaymentStatus.PENDING))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.MINIMUM_PAYMENT_AMOUNT_VIOLATION.getMessage());

        assertThatThrownBy(() -> new Payment(1L, -100L, PaymentStatus.PENDING))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.MINIMUM_PAYMENT_AMOUNT_VIOLATION.getMessage());
    }

    @Test
    @DisplayName("결제 상태가 null 이면 IllegalArgumentException을 던진다")
    void constructor_ShouldThrowException_WhenStatusIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> new Payment(1L, 5000L, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.PAYMENT_STATUS_REQUIRED.getMessage());
    }

    @Test
    @DisplayName("주문 객체의 상태를 변경할 수 있다")
    void updatePaymentStatus_ShouldChangeStatus() {
        // Arrange
        Payment payment = new Payment(1L, 5000L, PaymentStatus.PENDING);

        // Act
        payment.updatePaymentStatus(PaymentStatus.COMPLETED);

        // Assert
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
    }

    @Test
    @DisplayName("결제 상태가 null 이면 IllegalArgumentException을 던진다")
    void updatePaymentStatus_ShouldThrowException_WhenStatusIsNull() {
        // Arrange
        Payment payment = new Payment(1L, 5000L, PaymentStatus.PENDING);

        // Act & Assert
        assertThatThrownBy(() -> payment.updatePaymentStatus(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.PAYMENT_STATUS_REQUIRED.getMessage());
    }
}
