package kr.hhplus.be.server.application.order.facade;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.application.order.dto.OrderAndPaymentResultDto;
import kr.hhplus.be.server.application.order.dto.OrderDto;
import kr.hhplus.be.server.application.order.dto.OrderItemDto;
import kr.hhplus.be.server.application.order.dto.ProductSalesDto;
import kr.hhplus.be.server.application.payment.PaymentFacade;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.entity.CouponIssuance;
import kr.hhplus.be.server.domain.coupon.exception.CouponExpiredException;
import kr.hhplus.be.server.domain.coupon.service.CouponIssuanceService;
import kr.hhplus.be.server.domain.coupon.service.CouponService;
import kr.hhplus.be.server.domain.order.entity.OrderDetail;
import kr.hhplus.be.server.domain.order.entity.Orders;
import kr.hhplus.be.server.domain.order.repository.OrderDetailRepository;
import kr.hhplus.be.server.domain.order.service.OrderDetailService;
import kr.hhplus.be.server.domain.order.service.OrderService;
import kr.hhplus.be.server.domain.payment.entity.Payment;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.service.ProductService;
import kr.hhplus.be.server.domain.user.entity.Users;
import kr.hhplus.be.server.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderFacade {
    private final UserService userService;
    private final ProductService productService;
    private final CouponIssuanceService couponIssuanceService;
    private final CouponService couponService;
    private final OrderService orderService;
    private final OrderDetailService orderDetailService;
    private final PaymentFacade paymentFacade;
    private final OrderDetailRepository orderDetailRepository;

    @Transactional
    public OrderAndPaymentResultDto makeOrder(OrderDto orderDto) {
        // 사용자 조회
        Users user = userService.getUserById(orderDto.getUserId());
        // 주문 생성
        Orders order = orderService.createOrder(orderDto.getUserId(), orderDto.getCouponIssuanceId());
        List<OrderDetail> orderDetailList = new ArrayList<>();
        for (OrderItemDto orderProduct : orderDto.getOrderItemDtoList()) {
            // 재고 확인 및 감량
            Product product = productService.validateStockAndReduceQuantityWithLock(orderProduct.getProductId(), orderProduct.getQuantity());
            // orderDetail 생성
            OrderDetail orderDetail = new OrderDetail(order.getId(), product, orderProduct.getQuantity());
            orderDetailList.add(orderDetail);
            // 총 주문 금액 추가
            order.addOrderAmount(orderDetail.getSubTotal());
        }
        orderService.save(order);
        orderDetailService.saveAll(orderDetailList);

        // 쿠폰 적용
        if (orderDto.getCouponIssuanceId() != null) {
            applyCouponToOrder(order, orderDto.getCouponIssuanceId());
        }
        //결제
        Payment payment = paymentFacade.processPayment(order);

        return OrderAndPaymentResultDto.fromEntity(order, payment);
    }

    // 쿠폰 적용
    private void applyCouponToOrder(Orders order, String couponIssuanceId) {
        CouponIssuance couponIssuance = couponIssuanceService.getById(couponIssuanceId);
        Coupon coupon = couponService.getCouponById(couponIssuance.getCouponId());

        if (!order.getUserId().equals(couponIssuance.getUserId())) {
            throw new IllegalArgumentException("본인이 발급한 쿠폰만 사용할 수 있습니다.");
        }
        if (order.getTotalAmount() < coupon.getMinimumOrderAmount()) {
            throw new IllegalArgumentException("쿠폰을 적용할 수 있는 최소 주문금액 이하입니다.");
        }
        if (coupon.isExpired()) {
            throw new CouponExpiredException();
        }

        order.applyCoupon(coupon.getDiscountAmount());
        couponIssuance.changeStatusToUsed();
        couponIssuanceService.save(couponIssuance); // 쿠폰 상태 업데이트
    }

    // 판매량 상위 5개 목록 조회
    public List<ProductSalesDto> getTopSellingProducts() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(3);
        Pageable pageable = PageRequest.of(0, 5);

        // Step 1: 최근 3일간 판매량 상위 제품 조회
        List<Object[]> results = orderDetailRepository.findTopSellingProducts(startDate, pageable);

        // Step 2: productId 리스트 추출
        List<Long> productIds = results.stream()
                .map(row -> (Long) row[0]) // productId 추출
                .toList();

        // Step 3: ProductEntity 한 번에 조회
        Map<Long, Product> productMap = productService.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        // Step 4: ProductSalesResponse 생성 및 반환
        return results.stream()
                .map(row -> {
                    Long productId = (Long) row[0];
                    Integer soldQuantity = (Integer) row[1];
                    Product product = productMap.get(productId);

                    if (product == null) {
                        throw new IllegalArgumentException("Product not found for id: " + productId);
                    }

                    return new ProductSalesDto(
                            product.getId(),
                            product.getName(),
                            product.getPrice(),
                            product.getStock(),
                            soldQuantity
                    );
                })
                .toList();
    }
}
