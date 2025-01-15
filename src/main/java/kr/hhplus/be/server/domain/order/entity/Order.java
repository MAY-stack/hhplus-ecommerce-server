package kr.hhplus.be.server.domain.order.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.common.exception.ErrorMessage;
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
@Table(name = "Orders")
public class Order {
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

    public Order(String userId, String couponIssuanceId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException(ErrorMessage.USER_ID_REQUIRED.getMessage());
        }
        this.userId = userId;
        this.couponIssuanceId = couponIssuanceId;
        this.totalAmount = 0L;
        this.finalAmount = 0L;
        this.status = OrderStatus.CREATED;
    }

    public void addOrderAmount(Long amount) {
        this.totalAmount += amount; // 총 금액 업데이트
    }

    public void updateFinalAmount(Long finalAmount) {
        this.discountAmount = this.totalAmount - finalAmount;
        this.finalAmount = finalAmount;
    }

    public void updateStatus(OrderStatus status) {
        this.status = status;
    }
}
