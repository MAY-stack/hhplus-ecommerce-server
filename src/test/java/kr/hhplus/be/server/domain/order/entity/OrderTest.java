package kr.hhplus.be.server.domain.order.entity;

import kr.hhplus.be.server.common.exception.ErrorMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTest {

    @Test
    @DisplayName("사용자 아이디와 쿠폰발급 아이디로 주문 객체를 생성하면 초기값이 설정된 Order 객체를 반환한다")
    void constructor_ShouldInitializeFieldsCorrectly() {
        // Arrange
        String userId = "user123";
        String couponIssuanceId = "coupon123";

        // Act
        Order order = new Order(userId, couponIssuanceId);

        // Assert
        assertThat(order.getUserId()).isEqualTo(userId);
        assertThat(order.getCouponIssuanceId()).isEqualTo(couponIssuanceId);
        assertThat(order.getTotalAmount()).isEqualTo(0L);
        assertThat(order.getFinalAmount()).isEqualTo(0L);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CREATED);
    }

    @Test
    @DisplayName("사용자 아이디가 null 이면 IllegalArgumentException을 던진다")
    void constructor_ShouldThrowException_WhenUserIdIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> new Order(null, "coupon123"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.USER_ID_REQUIRED.getMessage());
    }

    @Test
    @DisplayName("사용자 아이디가 빈값이면 IllegalArgumentException을 던진다")
    void constructor_ShouldThrowException_WhenUserIdIsEmpty() {
        // Act & Assert
        assertThatThrownBy(() -> new Order("   ", "coupon123"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.USER_ID_REQUIRED.getMessage());
    }

    @Test
    @DisplayName("주문의 합계에 주문 금액을 추가할 수 있다")
    void addOrderAmount_ShouldIncreaseTotalAmount() {
        // Arrange
        Order order = new Order("user123", "coupon123");

        // Act
        order.addOrderAmount(5000L);
        order.addOrderAmount(3000L);

        // Assert
        assertThat(order.getTotalAmount()).isEqualTo(8000L);
    }

    @Test
    @DisplayName("할인을 적용한 최종 결제 금액을 설정할 수 있다")
    void updateFinalAmount_ShouldSetDiscountAndFinalAmount() {
        // Arrange
        Order order = new Order("user123", "coupon123");
        order.addOrderAmount(10000L);

        // Act
        order.updateFinalAmount(7000L);

        // Assert
        assertThat(order.getDiscountAmount()).isEqualTo(3000L);
        assertThat(order.getFinalAmount()).isEqualTo(7000L);
    }

    @Test
    @DisplayName("최종 결제 금액이 총 주문 금액과 같으면 할인 금액을 0으로 설정한다")
    void updateFinalAmount_ShouldSetDiscountToZero_WhenFinalAmountEqualsTotalAmount() {
        // Arrange
        Order order = new Order("user123", "coupon123");
        order.addOrderAmount(5000L);

        // Act
        order.updateFinalAmount(5000L);

        // Assert
        assertThat(order.getDiscountAmount()).isEqualTo(0L);
        assertThat(order.getFinalAmount()).isEqualTo(5000L);
    }

    @Test
    @DisplayName("주문완료로 상태를 변경할 수 있다")
    void updateStatus_ShouldChangeOrderStatus() {
        // Arrange
        Order order = new Order("user123", "coupon123");

        // Act
        order.updateStatus(OrderStatus.COMPLETED);

        // Assert
        assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }
}
