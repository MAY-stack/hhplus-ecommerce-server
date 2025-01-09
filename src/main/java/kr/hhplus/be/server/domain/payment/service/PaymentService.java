package kr.hhplus.be.server.domain.payment.service;

import kr.hhplus.be.server.domain.payment.entity.Payment;
import org.springframework.stereotype.Service;

@Service
public interface PaymentService {
    Payment save(Payment payment);
}
