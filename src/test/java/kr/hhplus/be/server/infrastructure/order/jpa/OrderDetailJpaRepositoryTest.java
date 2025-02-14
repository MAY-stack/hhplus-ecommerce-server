package kr.hhplus.be.server.infrastructure.order.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kr.hhplus.be.server.domain.order.entity.OrderDetail;
import kr.hhplus.be.server.domain.order.repository.OrderDetailRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@TestPropertySource(properties = "spring.jpa.properties.hibernate.jdbc.batch_size=30")
public class OrderDetailJpaRepositoryTest {

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private LocalDateTime startDate;

    @BeforeEach
    void setUp() {
        startDate = LocalDateTime.now().minusDays(3);

        for (int i = 1; i <= 10000; i++) {
            Long orderId = (long) ((i % 500) + 1); // 기존에 삽입한 `orders`의 ID 사용
            Long productId = (long) (i % 100);
            String productName = "Product " + productId;
            Integer quantity = (int) (Math.random() * 10) + 1;
            Long unitPrice = 1000L; // 단가

            OrderDetail orderDetail = new OrderDetail(orderId, productId, productName, unitPrice, quantity);

            entityManager.persist(orderDetail);
        }
        entityManager.flush();
    }

    @Test
    @Transactional
    void testQueryPerformanceBeforeAndAfterIndexing() {
        // 1. 인덱스 없는 상태에서 실행 시간 측정 및 실행 계획 확인
        log.info("==== Before Indexing ====");
        logExecutionPlan();

        long startWithoutIndex = System.currentTimeMillis();
        List<Object[]> resultWithoutIndex = orderDetailRepository.findTopSellingProducts(startDate, Pageable.ofSize(5));
        long endWithoutIndex = System.currentTimeMillis();
        long durationWithoutIndex = endWithoutIndex - startWithoutIndex;

        log.info("Execution time without index: {} ms", durationWithoutIndex);

        // 2. 인덱스 추가
        log.info("==== Adding Indexes ====");
        entityManager.createNativeQuery("CREATE INDEX idx_order_detail_created_at ON order_detail (created_at, product_id, quantity)").executeUpdate();
        entityManager.flush();

        // 3. 인덱스 적용 후 실행 계획 확인
        log.info("==== After Indexing ====");
        logExecutionPlan();

        // 4. 인덱스 적용 후 실행 시간 측정
        long startWithIndex = System.currentTimeMillis();
        List<Object[]> resultWithIndex = orderDetailRepository.findTopSellingProducts(startDate, Pageable.ofSize(5));
        long endWithIndex = System.currentTimeMillis();
        long durationWithIndex = endWithIndex - startWithIndex;

        log.info("Execution time with index: {} ms", durationWithIndex);

        // 5. 결과 검증
        assertThat(resultWithoutIndex).isNotEmpty();
        assertThat(resultWithIndex).isNotEmpty();

        // 6. 실행 시간 비교
        assertThat(durationWithIndex).isLessThan(durationWithoutIndex);
    }

    private void logExecutionPlan() {
        List<String> executionPlan = entityManager.createNativeQuery(
                "EXPLAIN ANALYZE SELECT product_id, SUM(quantity) AS total_quantity " +
                        "FROM order_detail " +
                        "WHERE created_at >= :startDate " +
                        "GROUP BY product_id " +
                        "ORDER BY total_quantity DESC " +
                        "LIMIT 5"
        ).setParameter("startDate", startDate).getResultList();

        executionPlan.forEach(plan -> log.info("Execution Plan: {}", plan));
    }


}

