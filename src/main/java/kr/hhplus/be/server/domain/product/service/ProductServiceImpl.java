package kr.hhplus.be.server.domain.product.service;

import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.exception.ProductNotFoundException;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    // 제품 생성
    @Override
    public Product createProduct(String name, Long price, Integer stock, Long categoryId) {
        Product product = new Product(name, price, stock, categoryId);
        return productRepository.save(product);
    }

    // 제품 조회 및 재고 수량 감소
    @Override
    public Product validateStockAndReduceQuantityWithLock(Long productId, Integer quantity) {
        Product product = productRepository.findByIdWithLock(productId)
                .orElseThrow(ProductNotFoundException::new);
        product.reduceStock(quantity);
        return null;
    }

    // 제품 조회
    @Override
    public List<Product> getProductList(Long categoryId) {
        if (categoryId == null) {
            return productRepository.findAll();
        } else {
            return productRepository.findAllByCategoryId(categoryId);
        }
    }

    @Override
    public List<Product> findAllById(List<Long> productIds) {
        return productRepository.findAllById(productIds);
    }


}
