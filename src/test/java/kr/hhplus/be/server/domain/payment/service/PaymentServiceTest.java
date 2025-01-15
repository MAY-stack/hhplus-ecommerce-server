package kr.hhplus.be.server.domain.payment.service;

import kr.hhplus.be.server.domain.payment.entity.Payment;
import kr.hhplus.be.server.domain.payment.entity.PaymentStatus;
import kr.hhplus.be.server.domain.payment.repository.PaymentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    @DisplayName("사용자 아이디, 결제금액, 결제 상태로 Payment객체를 생성할 수 있다")
    void createPayment_ShouldReturnSavedPayment() {
        // Arrange
        Long orderId = 1L;
        Long amount = 10000L;
        PaymentStatus status = PaymentStatus.PENDING;

        Payment expectedPayment = new Payment(orderId, amount, status);

        when(paymentRepository.save(any(Payment.class))).thenReturn(expectedPayment);

        // Act
        Payment actualPayment = paymentService.createPayment(orderId, amount, status);

        // Assert
        assertThat(actualPayment).isEqualTo(expectedPayment);
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    @DisplayName("Payment 객체와 결제상태로 객체의 결제상태를 변경할 수 있다")
    void updateStatus_ShouldUpdateAndReturnUpdatedPayment() {
        // Arrange
        Payment payment = new Payment(1L, 10000L, PaymentStatus.PENDING);
        PaymentStatus newStatus = PaymentStatus.COMPLETED;

        Payment updatedPayment = new Payment(1L, 10000L, newStatus);

        when(paymentRepository.save(payment)).thenReturn(updatedPayment);

        // Act
        Payment actualPayment = paymentService.updateStatus(payment, newStatus);

        // Assert
        assertThat(actualPayment.getStatus()).isEqualTo(newStatus);
        verify(paymentRepository, times(1)).save(payment);
    }
}
