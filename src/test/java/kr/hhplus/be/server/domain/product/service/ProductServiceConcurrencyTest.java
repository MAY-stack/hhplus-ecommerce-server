package kr.hhplus.be.server.domain.product.service;

import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
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

    @Test
    void 동시_재고_차감_테스트() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        List<Future<String>> results = new ArrayList<>();

        for (int i = 1; i <= THREAD_COUNT; i++) {
            Future<String> future = executorService.submit(() -> {
                try {
                    productService.validateStockAndReduceQuantityWithLock(productId, 1);
                    return "SUCCESS";
                } catch (Exception e) {
                    return "FAILED";
                }
            });
            results.add(future);
        }

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        // ✅ 테스트 검증
        long successCount = results.stream().filter(f -> {
            try {
                return f.get().equals("SUCCESS");
            } catch (Exception e) {
                return false;
            }
        }).count();

        long failCount = THREAD_COUNT - successCount;

        // ❗ 성공한 요청은 5건이어야 함 (재고가 5개였으므로)
        assertThat(successCount).isEqualTo(5);

        // ❗ 실패한 요청은 5건이어야 함 (초과 주문은 실패)
        assertThat(failCount).isEqualTo(THREAD_COUNT - 5);

        // ✅ 최종 재고가 0인지 확인
        Product updatedProduct = productRepository.findById(productId).orElseThrow();
        assertThat(updatedProduct.getStock()).isEqualTo(0);
    }
}
