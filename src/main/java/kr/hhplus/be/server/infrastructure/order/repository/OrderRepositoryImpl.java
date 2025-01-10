package kr.hhplus.be.server.infrastructure.order.repository;

import kr.hhplus.be.server.domain.order.entity.Orders;
import kr.hhplus.be.server.domain.order.repository.OrderRepository;
import kr.hhplus.be.server.infrastructure.order.jpa.OrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;

    @Override
    public Orders save(Orders orders) {
        return orderJpaRepository.save(orders);
    }
}
