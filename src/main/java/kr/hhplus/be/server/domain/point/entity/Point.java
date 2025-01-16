package kr.hhplus.be.server.domain.point.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.common.exception.ErrorMessage;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Point {

    public static final long MAX_SINGLE_RECHARGE = 1_000_000L; // 1회 최대 충전 금액
    public static final long MAX_BALANCE = 10_000_000L;       // 최대 잔액

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private Long balance = 0L;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // 사용자 ID로 Point 생성 (초기 잔액 0)
    public Point(String userId, Long initialBalance) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException(ErrorMessage.USER_ID_REQUIRED.getMessage());
        }
        if (initialBalance < 0) {
            throw new IllegalArgumentException(ErrorMessage.INITIAL_BALANCE_NEGATIVE.getMessage());
        }
        this.userId = userId;
        this.balance = initialBalance;
    }

    // 포인트 충전
    public Long addPoints(Long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException(ErrorMessage.MINIMUM_RECHARGE_POINTS_VIOLATION.getMessage());
        }
        if (amount > MAX_SINGLE_RECHARGE) {
            throw new IllegalArgumentException(ErrorMessage.MAXIMUM_RECHARGE_POINTS_VIOLATION.getMessage());
        }
        if (this.balance + amount > MAX_BALANCE) {
            throw new IllegalArgumentException(ErrorMessage.BALANCE_EXCEEDS_LIMIT.getMessage());
        }
        this.balance += amount;
        return this.balance;
    }

    // 포인트 사용
    public Long usePoints(Long amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException(ErrorMessage.MAXIMUM_USAGE_POINTS_VIOLATION.getMessage());
        }
        if (amount > MAX_BALANCE) {
            throw new IllegalArgumentException(ErrorMessage.MINIMUM_USAGE_POINTS_VIOLATION.getMessage());
        }
        if (this.balance < amount) {
            throw new IllegalArgumentException(ErrorMessage.INSUFFICIENT_POINT_BALANCE.getMessage());
        }
        this.balance -= amount;
        return this.balance;
    }

}
