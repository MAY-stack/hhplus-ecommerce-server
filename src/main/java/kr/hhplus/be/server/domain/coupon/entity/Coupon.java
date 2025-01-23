package kr.hhplus.be.server.domain.coupon.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.common.exception.ErrorMessage;
import lombok.*;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Builder
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false)
    private Long discountAmount;

    @Column(nullable = false)
    private Long minimumOrderAmount;

    @Column(nullable = false)
    private Integer remainingQuantity;

    @Column(nullable = false)
    private Integer totalQuantity;

    @Column(nullable = false)
    private LocalDate expirationDate;

//    @Version  // 낙관적 락 적용
//    private int version;

    public Coupon(String title, Long discountAmount, Long minimumOrderAmount, Integer totalQuantity, LocalDate expirationDate) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException(ErrorMessage.COUPON_TITLE_REQUIRED.getMessage());
        }
        if (discountAmount <= 0) {
            throw new IllegalArgumentException(ErrorMessage.MINIMUM_DISCOUNT_AMOUNT_VIOLATION.getMessage());
        }
        if (minimumOrderAmount <= 0) {
            throw new IllegalArgumentException(ErrorMessage.MINIMUM_ORDER_AMOUNT_VIOLATION.getMessage());
        }
        if (totalQuantity <= 0) {
            throw new IllegalArgumentException(ErrorMessage.MINIMUM_COUPON_QUANTITY_VIOLATION.getMessage());
        }
        if (expirationDate == null) { // null 검증 추가
            throw new NullPointerException(ErrorMessage.EXPIRATION_DATE_REQUIRE.getMessage());
        }
        if (expirationDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_EXPIRATION_DATE.getMessage());
        }
        this.title = title;
        this.discountAmount = discountAmount;
        this.minimumOrderAmount = minimumOrderAmount;
        this.totalQuantity = totalQuantity;
        this.remainingQuantity = totalQuantity;
        this.expirationDate = expirationDate;
    }

    public void decreaseRemaining() {
        // 쿠폰 만료 확인
        if (this.expirationDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(ErrorMessage.COUPON_EXPIRED.getMessage());
        }
        // 잔여 수량 확인
        if (this.remainingQuantity <= 0) {
            throw new IllegalArgumentException(ErrorMessage.COUPON_SOLD_OUT.getMessage());
        }
        this.remainingQuantity--;
    }
}



