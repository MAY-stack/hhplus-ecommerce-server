package kr.hhplus.be.server.infrastructure.order.jpa;

import kr.hhplus.be.server.domain.order.entity.OrderDetail;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderDetailJpaRepository extends JpaRepository<OrderDetail, Long> {
    @Query("""
                SELECT od.productId AS productId, SUM(od.quantity) AS totalQuantity
                FROM OrderDetail od
                JOIN Order o ON od.orderId = o.id
                WHERE o.createdAt >= :startDate
                GROUP BY od.productId
                ORDER BY totalQuantity DESC
            """)
    List<Object[]> findTopSellingProducts(@Param("startDate") LocalDateTime startDate, Pageable pageable);
}
