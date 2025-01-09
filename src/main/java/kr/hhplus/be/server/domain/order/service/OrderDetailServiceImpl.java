package kr.hhplus.be.server.domain.order.service;

import kr.hhplus.be.server.domain.order.entity.OrderDetail;
import kr.hhplus.be.server.domain.order.repository.OrderDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderDetailServiceImpl implements OrderDetailService {
    private final OrderDetailRepository orderDetailRepository;

    public void saveAll(List<OrderDetail> orderDetailList) {
        orderDetailRepository.saveAll(orderDetailList);
    }

    public List<Object[]> getTopSellingProducts(int days, int limit) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        Pageable pageable = PageRequest.of(0, limit);

        return orderDetailRepository.findTopSellingProducts(startDate, pageable);
    }
}
