package kr.hhplus.be.server.domain.product.repository;

import kr.hhplus.be.server.domain.product.entity.Category;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository {
    Category save(Category category);

    List<Category> saveAll(List<Category> categoryList);
}
