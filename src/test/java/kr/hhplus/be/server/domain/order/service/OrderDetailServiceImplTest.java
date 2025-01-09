package kr.hhplus.be.server.domain.order.service;

import kr.hhplus.be.server.domain.order.entity.OrderDetail;
import kr.hhplus.be.server.domain.order.repository.OrderDetailRepository;
import kr.hhplus.be.server.domain.product.entity.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderDetailServiceImplTest {

    @Mock
    private OrderDetailRepository orderDetailRepository;

    @InjectMocks
    private OrderDetailServiceImpl orderDetailService;

    @Test
    void 주문_상세_리스트를_한번에_저장할_수_있다() {
        // given
        Product productA = new Product("상품 A", 10000L, 10, 1L); // Product 객체 생성
        Product productB = new Product("상품 B", 15000L, 5, 2L);

        OrderDetail orderDetail1 = new OrderDetail(1L, productA, 2); // OrderDetail 생성
        OrderDetail orderDetail2 = new OrderDetail(1L, productB, 1);
        List<OrderDetail> orderDetails = Arrays.asList(orderDetail1, orderDetail2);

        // Mocking: saveAll 호출 설정
        doNothing().when(orderDetailRepository).saveAll(orderDetails);

        // Mocking: findAll 호출 설정
        when(orderDetailRepository.findAll()).thenReturn(orderDetails);

        // when
        orderDetailService.saveAll(orderDetails); // 리스트 저장

        // then
        List<OrderDetail> savedOrderDetails = orderDetailRepository.findAll(); // 저장된 데이터 조회
        assertEquals(2, savedOrderDetails.size());
        assertEquals("상품 A", savedOrderDetails.get(0).getProductName());
        assertEquals("상품 B", savedOrderDetails.get(1).getProductName());

        // Verify 호출 확인
        verify(orderDetailRepository, times(1)).saveAll(orderDetails);
        verify(orderDetailRepository, times(1)).findAll();
    }

    @Test
    void 인기_상품_조회_테스트() {
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
