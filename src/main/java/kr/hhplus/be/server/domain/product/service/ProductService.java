package kr.hhplus.be.server.domain.product.service;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.common.exception.ErrorMessage;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    // 제품 생성
    public Product createProduct(String name, Long price, Integer stock, Long categoryId) {
        Product product = new Product(name, price, stock, categoryId);
        return productRepository.save(product);
    }

    // 제품 조회 및 재고 수량 감소 ( 비관적 락 )
    @Transactional
    public Product validateStockAndReduceQuantityWithLock(Long productId, Integer quantity) {
        Product product = productRepository.findByIdWithLock(productId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.PRODUCT_NOT_FOUND.getMessage()));
        product.reduceStock(quantity);
        return productRepository.save(product);
    }

    // 제품 조회 및 재고 수량 감소
    @Transactional
//    @Retryable(
//            retryFor = ObjectOptimisticLockingFailureException.class,
//            maxAttempts = 5,  // 최대 5번 재시도
//            backoff = @Backoff(delay = 100) // 100ms 대기 후 재시도
//    )
//    @DistributedLock(key = "Product_Stock")
    public void validateStockAndReduceQuantity(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.PRODUCT_NOT_FOUND.getMessage()));
        product.reduceStock(quantity);
        productRepository.save(product);
    }

    // 제품 목록 조회
    public Page<Product> getProductList(Long categoryId, Pageable pageable) {
        if (categoryId == null) {
            return productRepository.findAll(pageable);
        } else {
            return productRepository.findAllByCategoryId(categoryId, pageable);
        }
    }

    // 제품 아이디 목록으로 제품 목록 조회
    public List<Product> getProductsByIds(List<Long> productIds) {
        return productRepository.findAllById(productIds);
    }

}
