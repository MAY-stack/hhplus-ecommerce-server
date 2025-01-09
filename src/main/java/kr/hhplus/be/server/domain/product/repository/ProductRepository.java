package kr.hhplus.be.server.domain.product.repository;

import kr.hhplus.be.server.domain.product.entity.Product;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository {
    Product save(Product product);

    List<Product> saveAll(List<Product> productList);

    Optional<Product> findById(Long productId);

    Optional<Product> findByIdWithLock(Long productId);

    List<Product> findAll();

    List<Product> findAllByCategoryId(Long categoryId);

    List<Product> findAllById(List<Long> productIds);
}
