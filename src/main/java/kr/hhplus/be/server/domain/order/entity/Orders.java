package kr.hhplus.be.server.domain.order.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private Long totalAmount;

    @Column
    private Long discountAmount;

    @Column(nullable = false)
    private Long finalAmount;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column
    private String couponIssuanceId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    public Orders(String userId, String couponIssuanceId) {
        this.userId = userId;
        this.couponIssuanceId = couponIssuanceId;
        this.totalAmount = 0L;
        this.finalAmount = 0L;
        this.status = OrderStatus.CREATED;
    }

    public void addOrderAmount(Long amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("추가할 금액은 0보다 커야 합니다.");
        }

        this.totalAmount += amount; // 총 금액 업데이트

        if (this.finalAmount < 0) {
            throw new IllegalStateException("최종 금액은 0보다 작을 수 없습니다.");
        }
    }

    public void applyCoupon(Long discountAmount) {
        if (discountAmount == null || discountAmount < 0) {
            throw new IllegalArgumentException("할인 금액은 null이거나 음수일 수 없습니다.");
        }

        this.discountAmount = discountAmount;
        this.finalAmount = this.totalAmount - discountAmount;

        if (this.finalAmount < 0) {
            throw new IllegalArgumentException("최종 금액은 0보다 작을 수 없습니다.");
        }
    }
}
