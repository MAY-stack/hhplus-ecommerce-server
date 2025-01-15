package kr.hhplus.be.server.domain.point.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.common.exception.ErrorMessage;
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

    @CreationTimestamp
    private LocalDateTime createdAt;

    // 생성자
    public PointHistory(Long pointId, PointHistoryType type, Long amount) {
        if (pointId == null) {
            throw new NullPointerException(ErrorMessage.POINT_ID_REQUIRED.getMessage());
        }
        if (type == null) {
            throw new NullPointerException(ErrorMessage.POINT_HISTORY_TYPE_REQUIRED.getMessage());
        }
        if (amount == null) {
            throw new NullPointerException(ErrorMessage.POINT_REQUIRED.getMessage());
        }
        this.pointId = pointId;
        this.type = type;
        this.amount = amount;
    }
}
