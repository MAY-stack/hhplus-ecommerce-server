package kr.hhplus.be.server.domain.coupon.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Builder
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"userId", "couponId"})
})
public class CouponIssuance {
    @Id
    private String id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private Long couponId;

    @CreationTimestamp
    private LocalDateTime issuedAt;

    @Column
    private LocalDateTime usedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private IssuedCouponStatus status;

    public CouponIssuance(Long couponId, String userId) {
        validate(couponId.toString(), "couponId는 필수입니다.");
        validate(userId, "userId는 필수입니다.");

        this.id = UUID.randomUUID().toString(); // UUID 생성 및 할당
        this.couponId = couponId;
        this.userId = userId;
        this.status = IssuedCouponStatus.ISSUED;
    }

    // 유효성 검증
    private void validate(String value, String errorMessage) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    // 사용으로 상태 변경
    public void changeStatusToUsed() {
        if (IssuedCouponStatus.USED.equals(this.status)) {
            throw new IllegalArgumentException("이미 사용한 쿠폰입니다.");
        }
        this.status = IssuedCouponStatus.USED;
        this.usedAt = LocalDateTime.now();
    }

    // 만료로 상태 변경
    public void changeStatusExpire() {
        if (!IssuedCouponStatus.EXPIRED.equals(this.status)) {
            this.status = IssuedCouponStatus.EXPIRED;
        }
    }
}
