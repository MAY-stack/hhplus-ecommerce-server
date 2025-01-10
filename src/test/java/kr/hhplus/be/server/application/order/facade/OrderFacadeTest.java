package kr.hhplus.be.server.application.order.facade;

import kr.hhplus.be.server.application.order.dto.OrderAndPaymentResultDto;
import kr.hhplus.be.server.application.order.dto.OrderDto;
import kr.hhplus.be.server.application.order.dto.OrderItemDto;
import kr.hhplus.be.server.application.order.dto.ProductSalesDto;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.entity.CouponIssuance;
import kr.hhplus.be.server.domain.coupon.service.CouponIssuanceService;
import kr.hhplus.be.server.domain.coupon.service.CouponService;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.service.ProductService;
import kr.hhplus.be.server.domain.user.entity.Users;
import kr.hhplus.be.server.domain.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderFacadeTest {

    @Autowired
    private OrderFacade orderFacade;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CouponIssuanceService couponIssuanceService;

    @Autowired
    private CouponService couponService;

    Users user;
    Product product1;
    Product product2;
    Coupon coupon;
    CouponIssuance couponIssuance;

    @BeforeEach
    void setUp() {
        // 사용자 저장
        user = userService.createUser("testUser", "Test User");

        // 제품 저장
        product1 = productService.createProduct("Product 1", 1000L, 100, 1L);
        product2 = productService.createProduct("Product 2", 3000L, 50, 1L);

        // 쿠폰 발급
        coupon = couponService.createCoupon("Coupon 1", 500L, 1000L, 10, LocalDate.now().plusDays(7));
        couponIssuance = couponIssuanceService.issueCoupon(coupon, user);
    }

    @Test
    void 주문_생성_및_결제_통합_테스트() {
        // Arrange
        String userId = user.getId(); // 이미 저장된 사용자 ID
        Long productId = product1.getId(); // 저장된 제품 ID
        String couponIssuanceId = couponIssuance.getId(); // 발급된 쿠폰 ID

        OrderItemDto orderItemDto = new OrderItemDto(productId, 2); // 제품 2개 주문
        OrderDto orderDto = new OrderDto(userId, List.of(orderItemDto), couponIssuanceId);

        // Act
        OrderAndPaymentResultDto result = orderFacade.makeOrder(orderDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getOrderId()).isNotNull();
        assertThat(result.getPaymentStatus()).isNotNull();
    }

    @Test
    void 쿠폰이_만료된_경우_RuntimeException을_던진다() {
        // Arrange
        String userId = "testUser";
        Long productId = 1L;
        String expiredCouponId = "expiredCoupon"; // 만료된 쿠폰 ID

        OrderItemDto orderItemDto = new OrderItemDto(productId, 2);
        OrderDto orderDto = new OrderDto(userId, List.of(orderItemDto), expiredCouponId);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> orderFacade.makeOrder(orderDto));
    }

    @Test
    void 판매량_상위_5개_조회_테스트() {
        // Given
        Product product3 = productService.createProduct("Product 3", 3000L, 50, 1L);
        Product product4 = productService.createProduct("Product 4", 3000L, 50, 1L);
        Product product5 = productService.createProduct("Product 5", 3000L, 50, 1L);
        Product product6 = productService.createProduct("Product 6", 3000L, 50, 1L);

        List<OrderItemDto> orderItemDtoList = new ArrayList<>();
        orderItemDtoList.add(new OrderItemDto(product1.getId(), 2));
        orderItemDtoList.add(new OrderItemDto(product2.getId(), 3));
        orderItemDtoList.add(new OrderItemDto(product3.getId(), 4));
        orderItemDtoList.add(new OrderItemDto(product4.getId(), 5));
        orderItemDtoList.add(new OrderItemDto(product5.getId(), 6));
        orderItemDtoList.add(new OrderItemDto(product6.getId(), 7));

        OrderDto orderDto = new OrderDto(user.getId(), orderItemDtoList, couponIssuance.getId());

        orderFacade.makeOrder(orderDto);

        // When
        List<ProductSalesDto> topSellingProducts = orderFacade.getTopSellingProducts();

        // Then
        assertAll(
                () -> assertThat(topSellingProducts).isNotNull(),
                () -> assertThat(topSellingProducts.size()).isLessThanOrEqualTo(5),
                () -> assertEquals(6L, topSellingProducts.get(0).getProductId()),
                () -> assertEquals(5L, topSellingProducts.get(1).getProductId()),
                () -> assertEquals(4L, topSellingProducts.get(2).getProductId()),
                () -> assertEquals(3L, topSellingProducts.get(3).getProductId()),
                () -> assertEquals(2L, topSellingProducts.get(4).getProductId())
        );


        // 제품의 판매량이 내림차순으로 정렬되어 있는지 확인
        for (int i = 1; i < topSellingProducts.size(); i++) {
            assertThat(topSellingProducts.get(i - 1).getSoldQuantity())
                    .isGreaterThanOrEqualTo(topSellingProducts.get(i).getSoldQuantity());
        }
    }
}
