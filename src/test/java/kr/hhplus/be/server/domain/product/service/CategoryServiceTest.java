package kr.hhplus.be.server.domain.product.service;

import kr.hhplus.be.server.domain.product.entity.Category;
import kr.hhplus.be.server.domain.product.repository.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    @DisplayName("유효한 카테고리명으로 카테고리를 생성하면 생성된 Category 객체를 반환한다")
    void createCategory_ShouldCreateAndReturnCategory_WhenValidNameProvided() {
        // Arrange
        String categoryName = "Electronics";
        Category category = new Category(categoryName);

        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        // Act
        Category createdCategory = categoryService.createCategory(categoryName);

        // Assert
        assertThat(createdCategory).isNotNull();
        assertThat(createdCategory.getName()).isEqualTo(categoryName);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

}
