package kr.hhplus.be.server.domain.order.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.common.exception.ErrorMessage;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long orderId;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false, length = 200)
    private String productName;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Long unitPrice;

    @Column(nullable = false)
    private Long subTotal;

    public OrderDetail(Long orderId, Long productId, String productName, Long unitPrice, Integer quantity) {
        if (orderId == null) {
            throw new IllegalArgumentException(ErrorMessage.ORDER_ID_REQUIRED.getMessage());
        }
        if (productId == null) {
            throw new IllegalArgumentException(ErrorMessage.PRODUCT_ID_REQUIRED.getMessage());
        }
        if (productName == null || productName.isBlank()) {
            throw new IllegalArgumentException(ErrorMessage.PRODUCT_NAME_REQUIRED.getMessage());
        }
        if (unitPrice == null || unitPrice <= 0) {
            throw new IllegalArgumentException(ErrorMessage.MINIMUM_PRODUCT_PRICE_VIOLATION.getMessage());
        }
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException(ErrorMessage.MINIMUM_ORDER_QUANTITY_VIOLATION.getMessage());
        }
        this.orderId = orderId;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subTotal = quantity * unitPrice;
    }
}
