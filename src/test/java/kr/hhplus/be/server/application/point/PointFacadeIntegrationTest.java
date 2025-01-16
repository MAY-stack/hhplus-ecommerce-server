package kr.hhplus.be.server.application.point;

import kr.hhplus.be.server.domain.point.entity.Point;
import kr.hhplus.be.server.domain.point.entity.PointHistory;
import kr.hhplus.be.server.domain.point.entity.PointHistoryType;
import kr.hhplus.be.server.domain.point.repository.PointHistoryRepository;
import kr.hhplus.be.server.domain.point.repository.PointRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PointFacadeIntegrationTest {

    @Autowired
    private PointFacade pointFacade;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private PointHistoryRepository pointHistoryRepository;

    @Test
    @DisplayName("포인트를 충전하면 충전된 포인트를 저장하고 포인트 내역을 저장한다")
    void rechargePointWithHistory_ShouldRechargePointAndSaveHistory() {
        // Arrange
        String userId = "testUser";
        Long initialBalance = 0L;
        Long rechargeAmount = 500L;

        // 데이터베이스에 초기 포인트 데이터 추가
        Point initialPoint = new Point(userId, initialBalance);
        pointRepository.save(initialPoint);

        // Act
        Point updatedPoint = pointFacade.rechargePointWithHistory(userId, rechargeAmount);

        // Assert: 포인트 검증
        Point savedPoint = pointRepository.findByUserId(userId).orElse(null);
        assertThat(savedPoint).isNotNull();
        assertThat(savedPoint.getBalance()).isEqualTo(initialBalance + rechargeAmount);

        // Assert: 히스토리 검증
        PointHistory savedHistory = pointHistoryRepository.findAllByPointId(savedPoint.getId()).get(0);
        assertThat(savedHistory).isNotNull();
        assertThat(savedHistory.getType()).isEqualTo(PointHistoryType.RECHARGE);
        assertThat(savedHistory.getAmount()).isEqualTo(rechargeAmount);
    }
}