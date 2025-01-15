package kr.hhplus.be.server.domain.coupon.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.common.exception.ErrorMessage;
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
        if (couponId == null) {
            throw new IllegalArgumentException(ErrorMessage.COUPON_ID_REQUIRED.getMessage());
        }
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException(ErrorMessage.USER_ID_REQUIRED.getMessage());
        }
        this.id = UUID.randomUUID().toString(); // UUID 생성 및 할당
        this.couponId = couponId;
        this.userId = userId;
        this.status = IssuedCouponStatus.ISSUED;
    }

    // 사용으로 상태 변경
    public void changeStatusToUsed(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException(ErrorMessage.USER_ID_REQUIRED.getMessage());
        }
        if (this.status.equals(IssuedCouponStatus.EXPIRED)) {
            throw new IllegalArgumentException(ErrorMessage.COUPON_EXPIRED.getMessage());
        }
        if (IssuedCouponStatus.USED.equals(this.status)) {
            throw new IllegalArgumentException(ErrorMessage.COUPON_ALREADY_USED.getMessage());
        }
        // 발급 받은 사용자와 userId 일치 여부 확인
        if (!this.userId.equals(userId)) {
            throw new IllegalArgumentException(ErrorMessage.COUPON_NOT_OWNED_BY_USER.getMessage());
        }
        this.status = IssuedCouponStatus.USED;
        this.usedAt = LocalDateTime.now();
    }

    // 만료로 상태 변경
    public void changeStatusExpire() {
        if (IssuedCouponStatus.USED.equals(this.status)) {
            throw new IllegalArgumentException(ErrorMessage.COUPON_ALREADY_USED.getMessage());
        }
        if (IssuedCouponStatus.EXPIRED.equals(this.status)) {
            throw new IllegalArgumentException(ErrorMessage.COUPON_EXPIRED.getMessage());
        }
        this.status = IssuedCouponStatus.EXPIRED;
    }
}
