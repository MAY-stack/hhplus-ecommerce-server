package kr.hhplus.be.server.domain.product.entity;

import kr.hhplus.be.server.common.exception.ErrorMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CategoryTest {

    @Test
    @DisplayName("유효한 이름으로 카테고리를 생성하면 Category 객체를 반환한다")
    void shouldCreateCategorySuccessfullyWhenNameIsValid() {
        // Arrange
        String validName = "Electronics";

        // Act
        Category category = new Category(validName);

        // Assert
        assertThat(category.getName()).isEqualTo(validName);
    }

    @Test
    @DisplayName("카테고리명이 null 이면 IllegalArgumentException을 던진다")
    void shouldThrowExceptionWhenNameIsNull() {
        // Arrange
        String nullName = null;

        // Act & Assert
        assertThatThrownBy(() -> new Category(nullName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.CATEGORY_NAME_REQUIRED.getMessage());
    }

    @Test
    @DisplayName("카테고리명이 빈값 이면 IllegalArgumentException을 던진다")
    void shouldThrowExceptionWhenNameIsBlank() {
        // Arrange
        String blankName = "  ";

        // Act & Assert
        assertThatThrownBy(() -> new Category(blankName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.CATEGORY_NAME_REQUIRED.getMessage());
    }

    @Test
    @DisplayName("카테고리명이 빈값 이면 IllegalArgumentException을 던진다2")
    void shouldThrowExceptionWhenNameIsEmpty() {
        // Arrange
        String emptyName = "";

        // Act & Assert
        assertThatThrownBy(() -> new Category(emptyName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.CATEGORY_NAME_REQUIRED.getMessage());
    }
}
