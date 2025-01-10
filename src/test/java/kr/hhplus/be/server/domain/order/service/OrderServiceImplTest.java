package kr.hhplus.be.server.domain.order.service;

import kr.hhplus.be.server.domain.order.entity.Orders;
import kr.hhplus.be.server.domain.order.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {
    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void 사용자_아이디로_주문정보를_저장하면_생성된_주문정보를_반환한다() {
        // given
        Orders order = new Orders("user123", null);
        when(orderRepository.save(order)).thenReturn(order);

        // when
        Orders savedOrder = orderService.save(order);

        // then
        assertEquals(order, savedOrder);
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void 사용자_아이디로_주문정보를_생성할_수_있다() {
        //given
        String userId = "user123";
        String couponIssuanceId = "coupon456";
        Orders order = new Orders(userId, couponIssuanceId);
        when(orderRepository.save(any(Orders.class))).thenReturn(order);

        //when
        Orders createdOrder = orderService.createOrder(userId, couponIssuanceId);

        // then
        assertEquals(userId, createdOrder.getUserId());
        assertEquals(couponIssuanceId, createdOrder.getCouponIssuanceId());
        verify(orderRepository, times(1)).save(any(Orders.class));
    }
}