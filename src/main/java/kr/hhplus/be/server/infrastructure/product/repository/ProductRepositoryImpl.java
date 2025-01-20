package kr.hhplus.be.server.infrastructure.product.repository;

import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import kr.hhplus.be.server.infrastructure.product.jpa.ProductJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;

    @Override
    public Product save(Product product) {
        return productJpaRepository.save(product);
    }

    @Override
    public List<Product> saveAll(List<Product> productList) {
        return productJpaRepository.saveAll(productList);
    }

    @Override
    public Optional<Product> findById(Long productId) {
        return productJpaRepository.findById(productId);
    }

    @Override
    public Optional<Product> findByIdWithLock(Long productId) {
        return productJpaRepository.findProductByIdWithLock(productId);
    }

    @Override
    public Page<Product> findAll(Pageable pageable) {
        return productJpaRepository.findAll(pageable);
    }

    @Override
    public Page<Product> findAllByCategoryId(Long categoryId, Pageable pageable) {
        return productJpaRepository.findAllByCategoryId(categoryId, pageable);
    }

    @Override
    public List<Product> findAllById(List<Long> productIds) {
        return productJpaRepository.findAllById(productIds);
    }

    @Override
    public void deleteAllInBatch() {
        productJpaRepository.deleteAllInBatch();
    }
}
