package kr.hhplus.be.server.domain.product.service;

import kr.hhplus.be.server.domain.product.entity.Product;

import java.util.List;

public interface ProductService {
    Product createProduct(String name, Long price, Integer stock, Long categoryId);

    Product validateStockAndReduceQuantityWithLock(Long productId, Integer quantity);

    // 제품 조회
    List<Product> getProductList(Long categoryId);

    List<Product> findAllById(List<Long> productIds);
}
