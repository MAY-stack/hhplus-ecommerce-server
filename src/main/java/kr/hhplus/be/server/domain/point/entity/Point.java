package kr.hhplus.be.server.domain.point.entity;

import jakarta.persistence.*;
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
    public Point(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("사용자 아이디는 필수입니다.");
        }
        this.userId = userId;
    }

    // 사용자 ID와 초기 잔액으로 Point 생성
    public Point(String userId, Long balance) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("사용자 아이디는 필수입니다.");
        }
        if (balance == null || balance < 0) {
            throw new IllegalArgumentException("초기 잔액은 0 이상이어야 합니다. 입력된 값: " + balance);
        }
        this.userId = userId;
        this.balance = balance;
    }

    // 포인트 충전
    public void addPoints(Long amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("충전 금액은 0보다 커야 합니다. 입력된 값: " + amount);
        }
        if (amount > MAX_SINGLE_RECHARGE) {
            throw new IllegalArgumentException(
                    "1회 최대 충전 금액은 " + MAX_SINGLE_RECHARGE + " 포인트를 초과할 수 없습니다. 입력된 값: " + amount
            );
        }
        if (balance + amount > MAX_BALANCE) {
            throw new IllegalArgumentException(
                    "최대 보유 잔고는 " + MAX_BALANCE + " 포인트 미만입니다. 현재 잔고: " + balance + ", 입력된 충전 금액: " + amount
            );
        }
        this.balance += amount;
    }

    // 포인트 사용 메서드
    public void usePoints(Long amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("사용 금액은 0보다 커야 합니다. 입력된 값: " + amount);
        }
        if (amount > balance) {
            throw new IllegalArgumentException(
                    "잔액이 부족합니다. 현재 잔고: " + balance + ", 입력된 사용 금액: " + amount
            );
        }
        this.balance -= amount;
    }

}
