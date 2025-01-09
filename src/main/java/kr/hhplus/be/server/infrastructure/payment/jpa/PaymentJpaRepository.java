package kr.hhplus.be.server.infrastructure.payment.jpa;

import kr.hhplus.be.server.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {
}
