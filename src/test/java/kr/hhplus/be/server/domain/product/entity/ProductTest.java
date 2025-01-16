package kr.hhplus.be.server.domain.product.entity;

import kr.hhplus.be.server.common.exception.ErrorMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductTest {

    @Test
    @DisplayName("유효한 인자들로 상품 생성을 요청하면 생성된 Product 객체를 반환한다")
    void shouldCreateProductSuccessfullyWhenAllInputsAreValid() {
        // Arrange
        String name = "Laptop";
        Long price = 1500L;
        Integer stock = 10;
        Long categoryId = 1L;

        // Act
        Product product = new Product(name, price, stock, categoryId);

        // Assert
        assertThat(product.getName()).isEqualTo(name);
        assertThat(product.getPrice()).isEqualTo(price);
        assertThat(product.getStock()).isEqualTo(stock);
        assertThat(product.getCategoryId()).isEqualTo(categoryId);
    }

    @Test
    @DisplayName("상품명이 null이면 IllegalArgumentException을 던진다")
    void shouldThrowExceptionWhenNameIsNull() {
        // Arrange
        String name = null;
        Long price = 1500L;
        Integer stock = 10;
        Long categoryId = 1L;

        // Act & Assert
        assertThatThrownBy(() -> new Product(name, price, stock, categoryId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.PRODUCT_NAME_REQUIRED.getMessage());
    }

    @Test
    @DisplayName("상품명이 빈값이면 IllegalArgumentException을 던진다")
    void shouldThrowExceptionWhenNameIsBlank() {
        // Arrange
        String name = " ";
        Long price = 1500L;
        Integer stock = 10;
        Long categoryId = 1L;

        // Act & Assert
        assertThatThrownBy(() -> new Product(name, price, stock, categoryId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.PRODUCT_NAME_REQUIRED.getMessage());
    }

    @Test
    @DisplayName("가격이 0이하 이면 IllegalArgumentException을 던진다")
    void shouldThrowExceptionWhenPriceIsZeroOrNegative() {
        // Arrange
        String name = "Laptop";
        Long price = 0L;
        Integer stock = 10;
        Long categoryId = 1L;

        // Act & Assert
        assertThatThrownBy(() -> new Product(name, price, stock, categoryId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.MINIMUM_PRICE_VIOLATION.getMessage());
    }

    @Test
    @DisplayName("재고가 0이하 이면 IllegalArgumentException을 던진다")
    void shouldThrowExceptionWhenStockIsZeroOrNegative() {
        // Arrange
        String name = "Laptop";
        Long price = 1500L;
        Integer stock = 0;
        Long categoryId = 1L;

        // Act & Assert
        assertThatThrownBy(() -> new Product(name, price, stock, categoryId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.MINIMUM_STOCK_VIOLATION.getMessage());
    }

    @Test
    @DisplayName("카테고리 id가 null이면 IllegalArgumentException을 던진다")
    void shouldThrowExceptionWhenCategoryIdIsNull() {
        // Arrange
        String name = "Laptop";
        Long price = 1500L;
        Integer stock = 10;
        Long categoryId = null;

        // Act & Assert
        assertThatThrownBy(() -> new Product(name, price, stock, categoryId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.CATEGORY_ID_REQUIRED.getMessage());
    }

    @Test
    @DisplayName("재고가 주문량보다 많거나 같으면 재고 감량에 성공한다.")
    void reduceStock_ShouldReduceStockSuccessfullyWhenQuantityIsAvailable() {
        // Arrange
        String name = "Laptop";
        Long price = 1500L;
        Integer stock = 10;
        Long categoryId = 1L;
        Product product = new Product(name, price, stock, categoryId);

        // Act
        product.reduceStock(5);

        // Assert
        assertThat(product.getStock()).isEqualTo(5);
    }

    @Test
    @DisplayName("재고 < 주문량 이면 IllegalArgumentException을 던진다")
    void reduceStock_ShouldThrowExceptionWhenStockIsInsufficient() {
        // Arrange
        String name = "Laptop";
        Long price = 1500L;
        Integer stock = 10;
        Long categoryId = 1L;
        Product product = new Product(name, price, stock, categoryId);

        // Act & Assert
        assertThatThrownBy(() -> product.reduceStock(15))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.PRODUCT_SOLD_OUT.getMessage());
    }
}
