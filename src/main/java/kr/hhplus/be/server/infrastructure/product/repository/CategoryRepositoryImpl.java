package kr.hhplus.be.server.infrastructure.product.repository;

import kr.hhplus.be.server.domain.product.entity.Category;
import kr.hhplus.be.server.domain.product.repository.CategoryRepository;
import kr.hhplus.be.server.infrastructure.product.jpa.CategoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {
    private final CategoryJpaRepository categoryJpaRepository;

    @Override
    public Category save(Category category) {
        return categoryJpaRepository.save(category);
    }

    @Override
    public List<Category> saveAll(List<Category> categoryList) {
        return categoryJpaRepository.saveAll(categoryList);
    }

    @Override
    public void deleteAllInBatch() {
        categoryJpaRepository.deleteAllInBatch();
    }
}
