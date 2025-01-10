package kr.hhplus.be.server.domain.order.service;

import kr.hhplus.be.server.domain.order.entity.Orders;
import org.springframework.stereotype.Service;

@Service
public interface OrderService {

    Orders save(Orders order);

    Orders createOrder(String userId, String couponIssuanceId);
}
