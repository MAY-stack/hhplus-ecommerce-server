package kr.hhplus.be.server.domain.coupon.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.coupon.exception.CouponExpiredException;
import kr.hhplus.be.server.domain.coupon.exception.CouponSoldOutException;
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

    public Coupon(String title, Long discountAmount, Long minimumOrderAmount, Integer totalQuantity, LocalDate expirationDate) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("쿠폰명은 필수입니다.");
        }
        if (discountAmount <= 0) {
            throw new IllegalArgumentException("할인금액은 0보다 커야합니다.");
        }
        if (minimumOrderAmount <= 0) {
            throw new IllegalArgumentException("최소주문금액은 0보다 커야합니다.");
        }
        if (totalQuantity <= 0) {
            throw new IllegalArgumentException("쿠폰의 수량은 0보다 커야합니다.");
        }
        if (expirationDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("쿠폰의 만료일은 오늘 이후여야 합니다.");
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
        if (isExpired()) {
            throw new CouponExpiredException();
        }
        // 잔여 수량 확인
        if (getRemainingQuantity() <= 0) {
            throw new CouponSoldOutException();
        }
        this.remainingQuantity--;
    }

    // 쿠폰 만료 여부 확인
    public boolean isExpired() {
        return expirationDate.isBefore(LocalDate.now());
    }
}



