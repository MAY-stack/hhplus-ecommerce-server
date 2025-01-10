package kr.hhplus.be.server.domain.payment.service;

import kr.hhplus.be.server.domain.payment.entity.Payment;
import kr.hhplus.be.server.domain.payment.entity.PaymentStatus;
import kr.hhplus.be.server.domain.payment.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    void 결제를_저장할_수_있다() {
        // given
        Payment payment = new Payment(1L, 10000L, PaymentStatus.COMPLETED);
        when(paymentRepository.save(payment)).thenReturn(payment);

        // when
        Payment savedPayment = paymentService.save(payment);

        // then
        assertEquals(payment, savedPayment);
        verify(paymentRepository, times(1)).save(payment);
    }
}
