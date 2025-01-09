package kr.hhplus.be.server.domain.point.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
public class PointHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long pointId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PointHistoryType type;

    @Column(nullable = false)
    private Long amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PointHistoryStatus status;

    @CreationTimestamp
    private LocalDateTime createdAt;

    // 생성자
    public PointHistory(Long pointId, PointHistoryType type, Long amount, PointHistoryStatus status) {
        this.pointId = pointId;
        this.type = type;
        this.amount = amount;
        this.status = status;
    }

    // 포인트 충전 성공 기록
    public static PointHistory createRechargeSuccess(Long pointId, Long amount) {
        return new PointHistory(pointId, PointHistoryType.RECHARGE, amount, PointHistoryStatus.SUCCESS);
    }

    // 포인트 충전 실패 기록
    public static PointHistory createRechargeFailure(Long pointId, Long amount) {
        return new PointHistory(pointId, PointHistoryType.RECHARGE, amount, PointHistoryStatus.FAIL);
    }

    // 포인트 차감 성공 기록
    public static PointHistory createDeductSuccess(Long pointId, Long amount) {
        return new PointHistory(pointId, PointHistoryType.DEDUCT, amount, PointHistoryStatus.SUCCESS);
    }

    // 포인트 차감 실패 기록
    public static PointHistory createDeductFailure(Long pointId, Long amount) {
        return new PointHistory(pointId, PointHistoryType.DEDUCT, amount, PointHistoryStatus.FAIL);
    }
}
