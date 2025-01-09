package kr.hhplus.be.server.infrastructure.order.jpa;

import kr.hhplus.be.server.domain.order.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJpaRepository extends JpaRepository<Orders, Long> {
}
