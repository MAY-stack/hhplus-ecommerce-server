package kr.hhplus.be.server.domain.product.service;

import kr.hhplus.be.server.domain.product.entity.Category;
import kr.hhplus.be.server.domain.product.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    void 카테고리를_생성할_수_있다() {
        // given
        Category mockCategory = new Category("전자제품");
        doReturn(mockCategory).when(categoryRepository).save(any(Category.class));

        // when
        Category createdCategory = categoryService.createCategory("전자제품");

        // then
        assertEquals("전자제품", createdCategory.getName());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }
}
