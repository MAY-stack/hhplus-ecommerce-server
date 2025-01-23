package kr.hhplus.be.server.application.order.facade;

import kr.hhplus.be.server.application.external.dto.ExternalRequestDto;
import kr.hhplus.be.server.application.external.service.ExternalDataPlatformService;
import kr.hhplus.be.server.application.order.dto.OrderAndPaymentResultDto;
import kr.hhplus.be.server.application.order.dto.OrderDto;
import kr.hhplus.be.server.application.order.dto.OrderItemDto;
import kr.hhplus.be.server.application.order.dto.ProductSalesDto;
import kr.hhplus.be.server.domain.coupon.entity.CouponIssuance;
import kr.hhplus.be.server.domain.coupon.service.CouponIssuanceService;
import kr.hhplus.be.server.domain.coupon.service.CouponService;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.entity.OrderDetail;
import kr.hhplus.be.server.domain.order.entity.OrderStatus;
import kr.hhplus.be.server.domain.order.service.OrderDetailService;
import kr.hhplus.be.server.domain.order.service.OrderService;
import kr.hhplus.be.server.domain.payment.entity.Payment;
import kr.hhplus.be.server.domain.payment.entity.PaymentStatus;
import kr.hhplus.be.server.domain.payment.service.PaymentService;
import kr.hhplus.be.server.domain.point.entity.Point;
import kr.hhplus.be.server.domain.point.entity.PointHistoryType;
import kr.hhplus.be.server.domain.point.service.PointHistoryService;
import kr.hhplus.be.server.domain.point.service.PointService;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.service.ProductService;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderFacade {
    private final UserService userService;
    private final ProductService productService;
    private final CouponService couponService;
    private final CouponIssuanceService couponIssuanceService;
    private final OrderService orderService;
    private final OrderDetailService orderDetailService;
    private final PaymentService paymentService;
    private final PointService pointService;
    private final PointHistoryService pointHistoryService;
    private final ExternalDataPlatformService externalDataPlatformService;

    @Transactional
    public OrderAndPaymentResultDto makeOrderAndProcessPayment(OrderDto orderDto) {
        String couponIssuanceId = orderDto.couponIssuanceId();

        // 사용자 조회
        User user = userService.getUserById(orderDto.userId());
        String userId = user.getId();

        // 주문 생성
        Order order = orderService.createOrder(userId, couponIssuanceId);

        // 주문 상세 정보 생성
        for (OrderItemDto orderProduct : orderDto.orderItemDtoList()) {
            // 재고 확인 및 감량
            Product product = productService.validateStockAndReduceQuantityWithLock(orderProduct.productId(), orderProduct.quantity());

            // orderDetail 생성
            OrderDetail orderDetail = orderDetailService.createOrderDetail(order.getId(), product, orderProduct.quantity());

            // 총 주문 금액 추가
            orderService.addOrderAmount(order, orderDetail.getSubTotal());
        }
        // 할인전 최종 금액 설정
        Long finalAmount = order.getTotalAmount();

        // 쿠폰 적용
        if (couponIssuanceId != null) {
            CouponIssuance couponIssuance = couponIssuanceService.getCouponIssuanceById(couponIssuanceId);
            // 쿠폰 적용 금액 계산
            finalAmount = couponService.applyCoupon(order.getTotalAmount(), couponIssuance.getCouponId());
            // 사용자 확인 및 쿠폰 사용 처리
            couponIssuance = couponIssuanceService.useIssuedCoupon(couponIssuance, userId);
        }

        // 최종 금액 설정
        orderService.updateFinalAmount(order, finalAmount);
        orderService.updateStatus(order, OrderStatus.PENDING_PAYMENT);

        //결제
        Payment payment;
        try {
            // 포인트 차감 처리, 차감 기록
            Point point = pointService.deductPointWithLock(userId, finalAmount);
            pointHistoryService.createPointHistory(point.getId(), PointHistoryType.DEDUCT, finalAmount);

            // 결제 성공 처리
            payment = paymentService.createPayment(order.getId(), finalAmount, PaymentStatus.COMPLETED);
            order = orderService.updateStatus(order, OrderStatus.COMPLETED);

            // 외부 플랫폼 데이터 전송
            externalDataPlatformService.sendData(ExternalRequestDto.from(order));
        } catch (Exception e) {
            payment = paymentService.createPayment(order.getId(), finalAmount, PaymentStatus.FAILED);
            order = orderService.updateStatus(order, OrderStatus.PAYMENT_FAILED);
            throw e;
        }

        return OrderAndPaymentResultDto.from(order, payment);
    }

    // 판매량 상위 5개 목록 조회
    public List<ProductSalesDto> getTopSellingProducts() {
        // Step 1: 최근 3일간 판매량 상위 제품 조회
        List<Object[]> results = orderDetailService.getTopSellingProducts(3, 5);
        // Step 2: productId 리스트 추출
        List<Long> productIds = results.stream()
                .map(row -> (Long) row[0]) // productId 추출
                .toList();
        // Step 3: ProductEntity 한 번에 조회
        Map<Long, Product> productMap = productService.getProductsByIds(productIds).stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        // Step 4: ProductSalesResponse 생성 및 반환
        return results.stream()
                .map(row -> {
                    Long productId = (Long) row[0];
                    Integer soldQuantity = ((Long) row[1]).intValue();
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
