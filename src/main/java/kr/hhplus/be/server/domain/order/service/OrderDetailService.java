package kr.hhplus.be.server.domain.order.service;

import kr.hhplus.be.server.domain.order.entity.OrderDetail;
import kr.hhplus.be.server.domain.order.repository.OrderDetailRepository;
import kr.hhplus.be.server.domain.product.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderDetailService {
    private final OrderDetailRepository orderDetailRepository;

    public OrderDetail createOrderDetail(Long orderId, Product product, Integer orderQuantity) {
        OrderDetail orderDetail = new OrderDetail(orderId, product.getId(), product.getName(), product.getPrice(), orderQuantity);
        return orderDetailRepository.save(orderDetail);
    }

    public List<Object[]> getTopSellingProducts(int days, int limit) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        Pageable pageable = PageRequest.of(0, limit);

        return orderDetailRepository.findTopSellingProducts(startDate, pageable);
    }
}
