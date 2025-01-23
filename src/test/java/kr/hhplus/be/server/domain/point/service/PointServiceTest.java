package kr.hhplus.be.server.domain.point.service;

import kr.hhplus.be.server.common.exception.ErrorMessage;
import kr.hhplus.be.server.domain.point.entity.Point;
import kr.hhplus.be.server.domain.point.repository.PointHistoryRepository;
import kr.hhplus.be.server.domain.point.repository.PointRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @Mock
    private PointRepository pointRepository;

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    @InjectMocks
    private PointService pointService;

    @Test
    @DisplayName("사용자 아이디로 포인트 생성을 요청하면 생성한 Point 객체를 반환한다")
    void createPoint_ShouldCreateAndSavePoint_WhenUserIdIsProvided() {
        // Arrange
        String userId = "user123";
        Point point = new Point(userId, 0L);

        when(pointRepository.save(any(Point.class))).thenReturn(point);

        // Act
        Point createdPoint = pointService.createPoint(userId);

        // Assert
        assertThat(createdPoint).isNotNull();
        assertThat(createdPoint.getUserId()).isEqualTo(userId);
        assertThat(createdPoint.getBalance()).isEqualTo(0L);
        verify(pointRepository, times(1)).save(any(Point.class));
    }

    @Test
    @DisplayName("포인트 아이디로 조회하면 해당하는 포인트가 있으면 Point 객체를 반환한다")
    void getPointByUserId_ShouldReturnPoint_WhenPointExists() {
        // Arrange
        String userId = "user123";
        Point point = new Point(userId, 1000L);

        when(pointRepository.findByUserId(userId)).thenReturn(Optional.of(point));

        // Act
        Point retrievedPoint = pointService.getPointByUserId(userId);

        // Assert
        assertThat(retrievedPoint).isNotNull();
        assertThat(retrievedPoint.getUserId()).isEqualTo(userId);
        assertThat(retrievedPoint.getBalance()).isEqualTo(1000L);
        verify(pointRepository, times(1)).findByUserId(userId);
    }

    @Test
    @DisplayName("포인트 아이디로 조회하면 해당하는 포인트가 없으면 IllegalArgumentException를 던진다")
    void getPointByUserId_ShouldThrowException_WhenPointDoesNotExist() {
        // Arrange
        String userId = "user123";

        when(pointRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> pointService.getPointByUserId(userId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.POINT_NOT_FOUND.getMessage());
        verify(pointRepository, times(1)).findByUserId(userId);
    }

    @Test
    @DisplayName("유효한 인자로 포인트 충전을 요청하면 포인트를 충전하고 충전한 Point 객체를 반환한다")
    @Transactional
    void rechargePoint_ShouldAddPoints_WhenValidInputsAreProvided() {
        // Arrange
        String userId = "user123";
        Long amount = 500L;
        Point point = new Point(userId, 1000L);

        when(pointRepository.findByUserId(userId)).thenReturn(Optional.of(point));
        when(pointRepository.save(any(Point.class))).thenReturn(point);

        // Act
        Point updatedPoint = pointService.rechargePointWithLock(userId, amount);

        // Assert
        assertThat(updatedPoint.getBalance()).isEqualTo(1500L); // 1000 + 500
        verify(pointRepository, times(1)).findByUserId(userId);
        verify(pointRepository, times(1)).save(point);
    }

    @Test
    @DisplayName("사용자 아이디에 해당하는 포인트가 없으면 충전 요청은 IllegalArgumentException을 던진다")
    @Transactional
    void rechargePoint_ShouldThrowException_WhenPointDoesNotExist() {
        // Arrange
        String userId = "user123";
        Long amount = 500L;

        when(pointRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> pointService.rechargePointWithLock(userId, amount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.POINT_NOT_FOUND.getMessage());
        verify(pointRepository, times(1)).findByUserId(userId);
        verify(pointRepository, never()).save(any(Point.class));
    }

    @Test
    @DisplayName("유효한 인자로 포인트 사용을 요청하면 포인트를 사용하고 사용한 Point 객체를 반환한다")
    @Transactional
    void deductPoint_ShouldDeductPoints_WhenValidInputsAreProvided() {
        // Arrange
        String userId = "user123";
        Long amount = 500L;
        Point point = new Point(userId, 1000L);

        when(pointRepository.findByUserId(userId)).thenReturn(Optional.of(point));
        when(pointRepository.save(any(Point.class))).thenReturn(point);

        // Act
        Point updatedPoint = pointService.deductPointWithLock(userId, amount);

        // Assert
        assertThat(updatedPoint.getBalance()).isEqualTo(500L); // 1000 - 500
        verify(pointRepository, times(1)).findByUserId(userId);
        verify(pointRepository, times(1)).save(point);
    }

    @Test
    @DisplayName("사용자 아이디에 해당하는 포인트가 없으면 사용 요청은 IllegalArgumentException을 던진다")
    @Transactional
    void deductPoint_ShouldThrowException_WhenPointDoesNotExist() {
        // Arrange
        String userId = "user123";
        Long amount = 500L;

        when(pointRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> pointService.deductPointWithLock(userId, amount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.POINT_NOT_FOUND.getMessage());
        verify(pointRepository, times(1)).findByUserId(userId);
        verify(pointRepository, never()).save(any(Point.class));
    }

}