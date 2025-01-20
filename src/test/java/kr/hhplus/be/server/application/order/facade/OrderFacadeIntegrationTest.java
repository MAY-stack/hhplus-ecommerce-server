package kr.hhplus.be.server.application.order.facade;

import kr.hhplus.be.server.ServerApplication;
import kr.hhplus.be.server.application.order.dto.OrderAndPaymentResultDto;
import kr.hhplus.be.server.application.order.dto.OrderDto;
import kr.hhplus.be.server.application.order.dto.OrderItemDto;
import kr.hhplus.be.server.application.order.dto.ProductSalesDto;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.entity.CouponIssuance;
import kr.hhplus.be.server.domain.coupon.entity.IssuedCouponStatus;
import kr.hhplus.be.server.domain.coupon.repository.CouponIssuanceRepository;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.server.domain.order.entity.OrderDetail;
import kr.hhplus.be.server.domain.order.entity.OrderStatus;
import kr.hhplus.be.server.domain.order.repository.OrderDetailRepository;
import kr.hhplus.be.server.domain.order.repository.OrderRepository;
import kr.hhplus.be.server.domain.payment.entity.PaymentStatus;
import kr.hhplus.be.server.domain.point.entity.Point;
import kr.hhplus.be.server.domain.point.repository.PointRepository;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.domain.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ServerApplication.class)
class OrderFacadeIntegrationTest {

    @Autowired
    private OrderFacade orderFacade;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CouponIssuanceRepository couponIssuanceRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @AfterEach
    void cleanData() {
        userRepository.deleteAllInBatch();
        pointRepository.deleteAllInBatch();
        couponRepository.deleteAllInBatch();
        couponIssuanceRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        orderRepository.deleteAllInBatch();
        orderDetailRepository.deleteAllInBatch();
    }

    @Test
    void makeOrderAndProcessPayment_ShouldCompleteOrderAndDeductPoints() {
        // Arrange
        User user = userRepository.save(new User("testUser", "Test User"));
        Product product = productRepository.save(new Product("Product 1", 1000L, 50, 1L));
        Point point = pointRepository.save(new Point(user.getId(), 5000L));
        Coupon coupon = couponRepository.save(new Coupon("Test Coupon", 500L, 3000L, 100, LocalDate.now().plusDays(5)));
        CouponIssuance couponIssuance = couponIssuanceRepository.save(new CouponIssuance(coupon.getId(), user.getId()));

        OrderItemDto orderItem = new OrderItemDto(product.getId(), 3);
        OrderDto orderDto = new OrderDto(user.getId(), List.of(orderItem), couponIssuance.getId());

        // Act
        OrderAndPaymentResultDto result = orderFacade.makeOrderAndProcessPayment(orderDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.orderStatus()).isEqualTo(OrderStatus.COMPLETED);
        assertThat(result.paymentStatus()).isEqualTo(PaymentStatus.COMPLETED);

        // Verify point deduction
        Point updatedPoint = pointRepository.findByUserId(user.getId()).orElseThrow();
        assertThat(updatedPoint.getBalance()).isEqualTo(2500L); // Initial balance 5000 - 2500(order amount)

        // Verify coupon usage
        CouponIssuance updatedCouponIssuance = couponIssuanceRepository.findById(couponIssuance.getId()).orElseThrow();
        assertThat(updatedCouponIssuance.getStatus()).isEqualTo(IssuedCouponStatus.USED);

        // Verify stock reduction
        Product updatedProduct = productRepository.findById(product.getId()).orElseThrow();
        assertThat(updatedProduct.getStock()).isEqualTo(47); // Initial stock 50 - 3
    }

    @Test
    @DisplayName("물품 판매량이 많은 순서로 제품 리스트를 조회할 수 있다")
    void getTopSellingProducts_ShouldReturnTopProducts() {
        // Arrange
        Product product1 = productRepository.save(new Product("Product 1", 1000L, 50, 1L));
        Product product2 = productRepository.save(new Product("Product 2", 1500L, 30, 2L));
        Product product3 = productRepository.save(new Product("Product 3", 1500L, 20, 3L));
        Product product4 = productRepository.save(new Product("Product 4", 1500L, 20, 3L));
        Product product5 = productRepository.save(new Product("Product 5", 1500L, 20, 3L));

        OrderDetail orderDetail1 = orderDetailRepository.save(new OrderDetail(1L, product1.getId(), product1.getName(), product1.getPrice(), 10));
        OrderDetail orderDetail2 = orderDetailRepository.save(new OrderDetail(1L, product2.getId(), product2.getName(), product2.getPrice(), 20));
        OrderDetail orderDetail3 = orderDetailRepository.save(new OrderDetail(1L, product3.getId(), product3.getName(), product3.getPrice(), 30));
        OrderDetail orderDetail4 = orderDetailRepository.save(new OrderDetail(1L, product4.getId(), product4.getName(), product4.getPrice(), 40));
        OrderDetail orderDetail5 = orderDetailRepository.save(new OrderDetail(1L, product5.getId(), product5.getName(), product5.getPrice(), 50));

        // Act
        List<ProductSalesDto> topProducts = orderFacade.getTopSellingProducts();

        // Assert
        assertThat(topProducts).hasSize(5);
        assertThat(topProducts.get(0).productId()).isEqualTo(product5.getId());
        assertThat(topProducts.get(0).soldQuantity()).isEqualTo(50);
        assertThat(topProducts.get(1).productId()).isEqualTo(product4.getId());
        assertThat(topProducts.get(1).soldQuantity()).isEqualTo(40);
        assertThat(topProducts.get(2).productId()).isEqualTo(product3.getId());
        assertThat(topProducts.get(2).soldQuantity()).isEqualTo(30);
        assertThat(topProducts.get(3).productId()).isEqualTo(product2.getId());
        assertThat(topProducts.get(3).soldQuantity()).isEqualTo(20);
        assertThat(topProducts.get(4).productId()).isEqualTo(product1.getId());
        assertThat(topProducts.get(4).soldQuantity()).isEqualTo(10);
    }
}