package kr.hhplus.be.server.domain.product.service;

import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@ExtendWith(MockitoExtension.class)
class ProductServiceConcurrencyTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    private Long productId;
    private static final int THREAD_COUNT = 10; // 10명의 사용자가 동시에 요청

    @BeforeEach
    void setUp() {
        // 재고가 5개 있는 상품 생성
        Product product = productService.createProduct("테스트 상품", 10000L, 5, 1L);
        this.productId = product.getId();
    }

    @AfterEach
    void cleanData() {
        productRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("재고차감 동시성 테스트")
    void testConcurrentStockReduction() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        List<Future<String>> results = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        for (int i = 1; i <= THREAD_COUNT; i++) {
            int threadId = i;
            Future<String> future = executorService.submit(() -> {
                try {
//                    비관적 락
                    productService.validateStockAndReduceQuantityWithLock(productId, 1);
//                    productService.validateStockAndReduceQuantity(productId, 1);
                    log.info("[THREAD-{}] 요청 성공 - 재고 차감 완료", threadId);
                    return "SUCCESS";
                } catch (Exception e) {
                    log.info("[THREAD-{}] 요청 실패 - 예외 발생: {}", threadId, e.getMessage());
                    return "FAILED";
                }
            });
            results.add(future);
        }

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        long endTime = System.currentTimeMillis();

        // 성공 요청 개수 계산 (Future에서 결과값 가져오기)

        long successCount = results.stream().filter(f -> {
            try {
                return f.get().equals("SUCCESS");
            } catch (Exception e) {
                return false;
            }
        }).count();

        long failCount = THREAD_COUNT - successCount;

        log.info("[TEST END] 테스트 종료 - 성공 요청: {}, 실패 요청: {}, 실행 시간: {}ms",
                successCount, failCount, (endTime - startTime));

        Product updatedProduct = productRepository.findById(productId).orElseThrow();
        log.info("[RESULT] 최종 재고: {} (expected: 0)", updatedProduct.getStock());

        // 테스트 검증
        // 성공한 요청은 5건이어야 함 (재고가 5개였으므로)
        assertThat(successCount).isEqualTo(5);
        // 실패한 요청은 5건이어야 함 (초과 주문은 실패)
        assertThat(failCount).isEqualTo(THREAD_COUNT - 5);
        // 최종 재고가 0인지 확인
        assertThat(updatedProduct.getStock()).isEqualTo(0);
    }
}
