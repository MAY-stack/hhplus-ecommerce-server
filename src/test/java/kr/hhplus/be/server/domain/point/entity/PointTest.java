package kr.hhplus.be.server.domain.point.entity;

import kr.hhplus.be.server.common.exception.ErrorMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PointTest {

    @Test
    @DisplayName("유효한 사용자 아이디와 유효한 초기 잔액으로 포인트 생성을 요청하면 생성한 Point 객체를 반환한다")
    void shouldCreatePointWithInitialBalance_WhenUserIdAndValidBalanceProvided() {
        // Arrange
        String userId = "user123";
        Long initialBalance = 500L;

        // Act
        Point point = new Point(userId, initialBalance);

        // Assert
        assertThat(point.getUserId()).isEqualTo(userId);
        assertThat(point.getBalance()).isEqualTo(initialBalance);
    }

    @Test
    @DisplayName("사용자 아이디 null로 포인트 생성을 요청하면 IllegalArgumentException을 던진다 ")
    void shouldThrowException_WhenUserIdIsNull() {
        // Arrange
        String userId = null;
        Long initialBalance = 500L;

        // Act & Assert
        assertThatThrownBy(() -> new Point(userId, initialBalance))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.USER_ID_REQUIRED.getMessage());
    }

    @Test
    @DisplayName("사용자 아이디를 빈값으로 포인트 생성을 요청하면 IllegalArgumentException을 던진다")
    void shouldThrowException_WhenUserIdIsEmpty() {
        // Arrange
        String userId = " ";
        Long initialBalance = 500L;

        // Act & Assert
        assertThatThrownBy(() -> new Point(userId, initialBalance))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.USER_ID_REQUIRED.getMessage());
    }

    @Test
    @DisplayName("음수의 초기잔액으로 포인트 생성을 요청하면 IllegalArgumentException을 던진다")
    void shouldThrowException_WhenInitialBalanceIsNegative() {
        // Arrange
        String userId = "user123";
        Long initialBalance = -1L;

        // Act & Assert
        assertThatThrownBy(() -> new Point(userId, initialBalance))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.INITIAL_BALANCE_NEGATIVE.getMessage());
    }

    @Test
    @DisplayName("유효한 값의 포인트 충전을 요청하면 충전한 잔액을 반환한다")
    void addPoints_ShouldAddPoints_WhenAmountIsValid() {
        // Arrange
        String userId = "user123";
        Long initialBalance = 500L;
        Long rechargeAmount = 1000L;
        Point point = new Point(userId, initialBalance);

        // Act
        Long updatedBalance = point.addPoints(rechargeAmount);

        // Assert
        assertThat(updatedBalance).isEqualTo(1500L);
    }

    @Test
    @DisplayName("음수나 0 포인트를 충전 요청하면 IllegalArgumentException을 던진다")
    void addPoints_ShouldThrowException_WhenAmountIsZeroOrNegative() {
        // Arrange
        String userId = "user123";
        Long initialBalance = 500L;
        Point point = new Point(userId, initialBalance);

        // Act & Assert
        assertThatThrownBy(() -> point.addPoints(0L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.MINIMUM_RECHARGE_POINTS_VIOLATION.getMessage());

        assertThatThrownBy(() -> point.addPoints(-1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.MINIMUM_RECHARGE_POINTS_VIOLATION.getMessage());
    }

    @Test
    @DisplayName("1_000_000L포인트 이상을 충전 요청하면 IllegalArgumentException을 던진다")
    void addPoints_ShouldThrowException_WhenAmountExceedsMaxRechargeLimit() {
        // Arrange
        String userId = "user123";
        Long initialBalance = 500L;
        Long rechargeAmount = 1_000_001L;
        Point point = new Point(userId, initialBalance);

        // Act & Assert
        assertThatThrownBy(() -> point.addPoints(rechargeAmount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.MAXIMUM_RECHARGE_POINTS_VIOLATION.getMessage());
    }

    @Test
    @DisplayName("충전 후 잔액이 10_000_000L을 초과하면 IllegalArgumentException을 던진다")
    void addPoints_ShouldThrowException_WhenBalanceExceedsMaxLimit() {
        // Arrange
        String userId = "user123";
        Long initialBalance = 9_500_000L;
        Long rechargeAmount = 1_000_000L;
        Point point = new Point(userId, initialBalance);

        // Act & Assert
        assertThatThrownBy(() -> point.addPoints(rechargeAmount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.BALANCE_EXCEEDS_LIMIT.getMessage());
    }

    @Test
    @DisplayName("잔액 이하의 포인트를 사용요청하면 사용 후 잔액을 반환한다")
    void usePoints_ShouldDeductPoints_WhenSufficientBalanceExists() {
        // Arrange
        String userId = "user123";
        Long initialBalance = 1500L;
        Long deductionAmount = 500L;
        Point point = new Point(userId, initialBalance);

        // Act
        Long updatedBalance = point.usePoints(deductionAmount);

        // Assert
        assertThat(updatedBalance).isEqualTo(1000L);
    }

    @Test
    @DisplayName("음수나 0포인트 사용을 요청하면 IllegalArgumentException을 던진다")
    void usePoints_ShouldThrowException_WhenAmountIsZeroOrNegative() {
        // Arrange
        String userId = "user123";
        Long initialBalance = 1500L;
        Point point = new Point(userId, initialBalance);

        // Act & Assert
        assertThatThrownBy(() -> point.usePoints(0L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.MAXIMUM_USAGE_POINTS_VIOLATION.getMessage());

        assertThatThrownBy(() -> point.usePoints(-1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.MAXIMUM_USAGE_POINTS_VIOLATION.getMessage());
    }

    @Test
    @DisplayName("잔액을 초과하는 포인트를 사용요청하면 IllegalArgumentException을 던진다")
    void usePoints_ShouldThrowException_WhenAmountExceedsBalance() {
        // Arrange
        String userId = "user123";
        Long initialBalance = 500L;
        Long deductionAmount = 1000L;
        Point point = new Point(userId, initialBalance);

        // Act & Assert
        assertThatThrownBy(() -> point.usePoints(deductionAmount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.INSUFFICIENT_POINT_BALANCE.getMessage());
    }
}
