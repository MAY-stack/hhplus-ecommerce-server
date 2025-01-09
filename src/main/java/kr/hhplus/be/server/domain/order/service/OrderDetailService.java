package kr.hhplus.be.server.domain.order.service;

import kr.hhplus.be.server.domain.order.entity.OrderDetail;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OrderDetailService {
    void saveAll(List<OrderDetail> orderDetailList);

    List<Object[]> getTopSellingProducts(int days, int limit);
}
