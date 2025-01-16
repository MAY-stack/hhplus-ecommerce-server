package kr.hhplus.be.server.domain.point.entity;

import kr.hhplus.be.server.common.exception.ErrorMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PointHistoryTest {

    @Test
    @DisplayName("유효한 인수로 포인트 내역 생성을 요청하면 생성된 PointHistory를 반환한다")
    void shouldCreatePointHistorySuccessfully_WhenValidInputsAreProvided() {
        // Arrange
        Long pointId = 1L;
        PointHistoryType type = PointHistoryType.RECHARGE;
        Long amount = 1000L;

        // Act
        PointHistory pointHistory = new PointHistory(pointId, type, amount);

        // Assert
        assertThat(pointHistory.getPointId()).isEqualTo(pointId);
        assertThat(pointHistory.getType()).isEqualTo(type);
        assertThat(pointHistory.getAmount()).isEqualTo(amount);
        assertThat(pointHistory.getCreatedAt()).isNull(); // Will be set by JPA during persistence
    }

    @Test
    @DisplayName("포인트 아이디가 null이면 NullPointerException을 던진다")
    void shouldThrowException_WhenPointIdIsNull() {
        // Arrange
        Long pointId = null;
        PointHistoryType type = PointHistoryType.RECHARGE;
        Long amount = 1000L;

        // Act & Assert
        assertThatThrownBy(() -> new PointHistory(pointId, type, amount))
                .isInstanceOf(NullPointerException.class)
                .hasMessage(ErrorMessage.POINT_ID_REQUIRED.getMessage());
    }

    @Test
    @DisplayName("내역 종류가 null이면 NullPointerException을 던진다")
    void shouldThrowException_WhenTypeIsNull() {
        // Arrange
        Long pointId = 1L;
        PointHistoryType type = null;
        Long amount = 1000L;

        // Act & Assert
        assertThatThrownBy(() -> new PointHistory(pointId, type, amount))
                .isInstanceOf(NullPointerException.class)
                .hasMessage(ErrorMessage.POINT_HISTORY_TYPE_REQUIRED.getMessage());
    }

    @Test
    @DisplayName("포인트가 null이면 NullPointerException을 던진다")
    void shouldThrowException_WhenAmountIsNull() {
        // Arrange
        Long pointId = 1L;
        PointHistoryType type = PointHistoryType.RECHARGE;
        Long amount = null;

        // Act & Assert
        assertThatThrownBy(() -> new PointHistory(pointId, type, amount))
                .isInstanceOf(NullPointerException.class)
                .hasMessage(ErrorMessage.POINT_REQUIRED.getMessage());
    }
}
