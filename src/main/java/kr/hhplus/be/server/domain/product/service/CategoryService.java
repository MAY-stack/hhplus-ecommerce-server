package kr.hhplus.be.server.domain.product.service;

import kr.hhplus.be.server.domain.product.entity.Category;
import org.springframework.stereotype.Service;

@Service
public interface CategoryService {
    Category createCategory(String categoryName);
}
