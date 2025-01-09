package kr.hhplus.be.server.domain.order.service;

import kr.hhplus.be.server.domain.order.entity.Orders;
import kr.hhplus.be.server.domain.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    public Orders save(Orders order) {
        return orderRepository.save(order);
    }

    public Orders createOrder(String userId, String couponIssuanceId) {
        Orders order = new Orders(userId, couponIssuanceId);
        return orderRepository.save(order);
    }
}
