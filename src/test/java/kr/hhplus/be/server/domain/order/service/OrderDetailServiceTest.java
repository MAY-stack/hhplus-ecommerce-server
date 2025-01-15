package kr.hhplus.be.server.domain.order.service;

import kr.hhplus.be.server.domain.order.entity.OrderDetail;
import kr.hhplus.be.server.domain.order.repository.OrderDetailRepository;
import kr.hhplus.be.server.domain.product.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderDetailServiceTest {

    @Mock
    private OrderDetailRepository orderDetailRepository;

    @InjectMocks
    private OrderDetailService orderDetailService;

    @Test
    @DisplayName("주문 상세정보 생성을 요청하면 생성된 OrderDetail을 반환한다")
    void createOrderDetail_ShouldReturnSavedOrderDetail() {
        // Arrange
        Long orderId = 1L;
        Product product = Product.builder()
                .id(1L)
                .name("Test Product")
                .price(2000L)
                .stock(100)
                .build();
        Integer orderQuantity = 5;

        OrderDetail expectedOrderDetail = new OrderDetail(orderId, product.getId(), product.getName(), product.getPrice(), orderQuantity);
        when(orderDetailRepository.save(any(OrderDetail.class))).thenReturn(expectedOrderDetail);

        // Act
        OrderDetail actualOrderDetail = orderDetailService.createOrderDetail(orderId, product, orderQuantity);

        // Assert
        assertThat(actualOrderDetail).isEqualTo(expectedOrderDetail);
        verify(orderDetailRepository, times(1)).save(any(OrderDetail.class));
    }

    @Test
    @DisplayName("날짜와 개수로 판매량 상위제품 조회를 하면 최근 입력한 기간동안 많이 팔린 상위 N개의 제품 리스트를 반환한다")
    void getTopSellingProducts_ShouldReturnTopSellingProducts() {
        // given
        int days = 7;
        int limit = 5;
        LocalDateTime mockStartDate = LocalDateTime.now().minusDays(days);
        Pageable pageable = PageRequest.of(0, limit);
        List<Object[]> mockResults = Collections.singletonList(new Object[]{"Product A", 100L});

        // Mocking
        when(orderDetailRepository.findTopSellingProducts(any(LocalDateTime.class), any(Pageable.class))).thenReturn(mockResults);

        // when
        List<Object[]> results = orderDetailService.getTopSellingProducts(days, limit);

        // then
        assertEquals(1, results.size());
        assertEquals("Product A", results.get(0)[0]);
        assertEquals(100L, results.get(0)[1]);
        verify(orderDetailRepository, times(1)).findTopSellingProducts(any(LocalDateTime.class), any(Pageable.class));
    }
}
