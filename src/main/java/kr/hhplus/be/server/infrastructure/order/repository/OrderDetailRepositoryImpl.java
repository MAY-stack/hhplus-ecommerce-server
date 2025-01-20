package kr.hhplus.be.server.infrastructure.order.repository;

import kr.hhplus.be.server.domain.order.entity.OrderDetail;
import kr.hhplus.be.server.domain.order.repository.OrderDetailRepository;
import kr.hhplus.be.server.infrastructure.order.jpa.OrderDetailJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderDetailRepositoryImpl implements OrderDetailRepository {
    private final OrderDetailJpaRepository orderDetailJpaRepository;

    @Override
    public OrderDetail save(OrderDetail orderDetail) {
        return orderDetailJpaRepository.save(orderDetail);
    }

    @Override
    public List<Object[]> findTopSellingProducts(LocalDateTime startDate, Pageable pageable) {
        return orderDetailJpaRepository.findTopSellingProductsByDate(startDate, pageable);
    }

    @Override
    public void deleteAllInBatch() {
        orderDetailJpaRepository.deleteAllInBatch();
    }
}
