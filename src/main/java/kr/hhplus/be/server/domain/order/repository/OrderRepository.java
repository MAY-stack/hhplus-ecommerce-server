package kr.hhplus.be.server.domain.order.repository;

import kr.hhplus.be.server.domain.order.entity.Orders;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository {
    Orders save(Orders orders);
}
