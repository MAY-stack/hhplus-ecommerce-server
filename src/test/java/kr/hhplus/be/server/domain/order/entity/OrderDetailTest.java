package kr.hhplus.be.server.domain.order.entity;

import kr.hhplus.be.server.common.exception.ErrorMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderDetailTest {

    @Test
    @DisplayName("유효한 인자로 주문 상세정보를 생성하면 OrderDetail을 반환한다")
    void constructor_ShouldInitializeFieldsCorrectly() {
        // Arrange
        Long orderId = 1L;
        Long productId = 100L;
        String productName = "Test Product";
        Long unitPrice = 1500L;
        Integer quantity = 3;

        // Act
        OrderDetail orderDetail = new OrderDetail(orderId, productId, productName, unitPrice, quantity);

        // Assert
        assertThat(orderDetail.getOrderId()).isEqualTo(orderId);
        assertThat(orderDetail.getProductId()).isEqualTo(productId);
        assertThat(orderDetail.getProductName()).isEqualTo(productName);
        assertThat(orderDetail.getUnitPrice()).isEqualTo(unitPrice);
        assertThat(orderDetail.getQuantity()).isEqualTo(quantity);
        assertThat(orderDetail.getSubTotal()).isEqualTo(unitPrice * quantity);
    }

    @Test
    @DisplayName("주문 아이디가 null이면 IllegalArgumentException을 던진다")
    void constructor_ShouldThrowException_WhenOrderIdIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> new OrderDetail(null, 100L, "Product", 1500L, 3))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.ORDER_ID_REQUIRED.getMessage());
    }

    @Test
    @DisplayName("제품 아이디가 null이면 IllegalArgumentException을 던진다")
    void constructor_ShouldThrowException_WhenProductIdIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> new OrderDetail(1L, null, "Product", 1500L, 3))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.PRODUCT_ID_REQUIRED.getMessage());
    }

    @Test
    @DisplayName("제품명이 null이면 IllegalArgumentException을 던진다")
    void constructor_ShouldThrowException_WhenProductNameIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> new OrderDetail(1L, 100L, null, 1500L, 3))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.PRODUCT_NAME_REQUIRED.getMessage());
    }

    @Test
    @DisplayName("제품명이 빈값이면 IllegalArgumentException을 던진다")
    void constructor_ShouldThrowException_WhenProductNameIsBlank() {
        // Act & Assert
        assertThatThrownBy(() -> new OrderDetail(1L, 100L, "   ", 1500L, 3))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.PRODUCT_NAME_REQUIRED.getMessage());
    }

    @Test
    @DisplayName("단가가 null이면 IllegalArgumentException을 던진다")
    void constructor_ShouldThrowException_WhenUnitPriceIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> new OrderDetail(1L, 100L, "Product", null, 3))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.MINIMUM_PRODUCT_PRICE_VIOLATION.getMessage());
    }

    @Test
    @DisplayName("단가가 1 미만 이면 IllegalArgumentException을 던진다")
    void constructor_ShouldThrowException_WhenUnitPriceIsZeroOrNegative() {
        // Act & Assert
        assertThatThrownBy(() -> new OrderDetail(1L, 100L, "Product", 0L, 3))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.MINIMUM_PRODUCT_PRICE_VIOLATION.getMessage());

        assertThatThrownBy(() -> new OrderDetail(1L, 100L, "Product", -1500L, 3))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.MINIMUM_PRODUCT_PRICE_VIOLATION.getMessage());
    }

    @Test
    @DisplayName("주문 수량이 null이면 IllegalArgumentException을 던진다")
    void constructor_ShouldThrowException_WhenQuantityIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> new OrderDetail(1L, 100L, "Product", 1500L, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.MINIMUM_ORDER_QUANTITY_VIOLATION.getMessage());
    }

    @Test
    @DisplayName("주문 수량이 1 미만이면 IllegalArgumentException을 던진다")
    void constructor_ShouldThrowException_WhenQuantityIsZeroOrNegative() {
        // Act & Assert
        assertThatThrownBy(() -> new OrderDetail(1L, 100L, "Product", 1500L, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.MINIMUM_ORDER_QUANTITY_VIOLATION.getMessage());

        assertThatThrownBy(() -> new OrderDetail(1L, 100L, "Product", 1500L, -3))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.MINIMUM_ORDER_QUANTITY_VIOLATION.getMessage());
    }
}
