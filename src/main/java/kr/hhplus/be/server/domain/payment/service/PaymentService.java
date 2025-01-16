package kr.hhplus.be.server.domain.payment.service;

import kr.hhplus.be.server.domain.payment.entity.Payment;
import kr.hhplus.be.server.domain.payment.entity.PaymentStatus;
import kr.hhplus.be.server.domain.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;

    public Payment createPayment(Long orderId, Long amount, PaymentStatus status) {
        return paymentRepository.save(new Payment(orderId, amount, status));
    }

    public Payment updateStatus(Payment payment, PaymentStatus status) {
        payment.updatePaymentStatus(status);
        return paymentRepository.save(payment);
    }
}
