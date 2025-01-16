package kr.hhplus.be.server.domain.product.service;

import kr.hhplus.be.server.domain.product.entity.Category;
import kr.hhplus.be.server.domain.product.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    
    public Category createCategory(String name) {
        Category category = new Category(name);
        return categoryRepository.save(category);
    }
}
