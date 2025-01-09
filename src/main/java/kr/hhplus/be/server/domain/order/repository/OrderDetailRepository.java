package kr.hhplus.be.server.domain.order.repository;

import kr.hhplus.be.server.domain.order.entity.OrderDetail;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderDetailRepository {
    void saveAll(List<OrderDetail> orderDetailList);

    List<OrderDetail> findAll();

    List<Object[]> findTopSellingProducts(LocalDateTime startDate, Pageable pageable);
}
