package kr.hhplus.be.server.domain.order.service;

import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.entity.OrderStatus;
import kr.hhplus.be.server.domain.order.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    @DisplayName("사용자 아이디와 CouponIssuanceId로 주문 생성을 요청하면 생성된 Order 객체를 반환한다")
    void createOrder_ShouldReturnSavedOrder() {
        // Arrange
        String userId = "user123";
        String couponIssuanceId = "coupon123";
        Order order = new Order(userId, couponIssuanceId);

        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Act
        Order createdOrder = orderService.createOrder(userId, couponIssuanceId);

        // Assert
        assertThat(createdOrder).isEqualTo(order);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("총 주문 금액에 금액 추가를 요청하면 총 주문금액에 추가된 Order 객체를 반환한다")
    void addOrderAmount_ShouldIncreaseTotalAmount() {
        // Arrange
        Order order = mock(Order.class);
        Long amount = 5000L;

        when(orderRepository.save(order)).thenReturn(order);

        // Act
        Order updatedOrder = orderService.addOrderAmount(order, amount);

        // Assert
        verify(order, times(1)).addOrderAmount(amount);
        assertThat(updatedOrder).isEqualTo(order);
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    @DisplayName("최종 금액 설정을 요청하면 최종 금액과 할인금액이 설정된 Order 객체를 반환한다")
    void updateFinalAmount_ShouldUpdateFinalAmountAndDiscount() {
        // Arrange
        Order order = mock(Order.class);
        Long finalAmount = 8000L;

        when(orderRepository.save(order)).thenReturn(order);

        // Act
        Order updatedOrder = orderService.updateFinalAmount(order, finalAmount);

        // Assert
        verify(order, times(1)).updateFinalAmount(finalAmount);
        assertThat(updatedOrder).isEqualTo(order);
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    @DisplayName("주문 상태를 변경 요청하면 주문 상태가 변경된 Order 객체를 반환한다")
    void updateStatus_ShouldChangeOrderStatus() {
        // Arrange
        Order order = mock(Order.class);
        OrderStatus status = OrderStatus.COMPLETED;

        when(orderRepository.save(order)).thenReturn(order);

        // Act
        Order updatedOrder = orderService.updateStatus(order, status);

        // Assert
        verify(order, times(1)).updateStatus(status);
        assertThat(updatedOrder).isEqualTo(order);
        verify(orderRepository, times(1)).save(order);
    }
}
