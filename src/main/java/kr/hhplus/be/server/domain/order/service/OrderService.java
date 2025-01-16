package kr.hhplus.be.server.domain.order.service;

import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.entity.OrderStatus;
import kr.hhplus.be.server.domain.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public Order createOrder(String userId, String couponIssuanceId) {
        Order order = new Order(userId, couponIssuanceId);
        return orderRepository.save(order);
    }

    public Order addOrderAmount(Order order, Long amount) {
        order.addOrderAmount(amount);
        return orderRepository.save(order);
    }

    public Order updateFinalAmount(Order order, Long finalAmount) {
        order.updateFinalAmount(finalAmount);
        return orderRepository.save(order);
    }

    public Order updateStatus(Order order, OrderStatus status) {
        order.updateStatus(status);
        return orderRepository.save(order);
    }
}
