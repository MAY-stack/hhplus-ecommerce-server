package kr.hhplus.be.server.domain.product.service;

import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ProductLockTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product("Test Product", 1000L, 10, 1L);
        productRepository.save(product);
    }

    @RepeatedTest(3)
        // 동일한 테스트 3번 반복하여 결과 비교
    void testPessimisticLockPerformance() throws InterruptedException {
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    productService.validateStockAndReduceQuantityWithLock(product.getId(), 1);
                } catch (Exception e) {
                    System.out.println("비관적 락 실패: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        long endTime = System.currentTimeMillis();

        Product updatedProduct = productRepository.findById(product.getId()).orElseThrow();
        System.out.println("🔒 비관적 락 - 남은 재고: " + updatedProduct.getStock() + " | 실행 시간: " + (endTime - startTime) + "ms");

        assertThat(updatedProduct.getStock()).isLessThanOrEqualTo(90); // 10개 감소 예상
    }

    @RepeatedTest(3)
    void testOptimisticLockPerformance() throws InterruptedException {
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        List<Exception> exceptions = new ArrayList<>();

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    productService.validateStockAndReduceQuantityWithOptimisticLock(product.getId(), 1);
                } catch (Exception e) {
                    exceptions.add(e);
                    System.out.println("낙관적 락 실패: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        long endTime = System.currentTimeMillis();

        Product updatedProduct = productRepository.findById(product.getId()).orElseThrow();
        System.out.println("🔄 낙관적 락 - 남은 재고: " + updatedProduct.getStock() + " | 실행 시간: " + (endTime - startTime) + "ms");
        System.out.println("❌ 충돌 발생 횟수: " + exceptions.size());

        assertThat(updatedProduct.getStock()).isLessThanOrEqualTo(90); // 일부 실패 가능성 있음
    }
}
