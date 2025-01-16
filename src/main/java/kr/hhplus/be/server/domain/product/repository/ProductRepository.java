package kr.hhplus.be.server.domain.product.repository;

import kr.hhplus.be.server.domain.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository {
    Product save(Product product);

    List<Product> saveAll(List<Product> productList);

    Optional<Product> findById(Long productId);

    Optional<Product> findByIdWithLock(Long productId);

    Page<Product> findAll(Pageable pageable);

    Page<Product> findAllByCategoryId(Long categoryId, Pageable pageable);

    List<Product> findAllById(List<Long> productIds);
}
