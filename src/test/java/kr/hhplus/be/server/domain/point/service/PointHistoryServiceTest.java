package kr.hhplus.be.server.domain.point.service;

import kr.hhplus.be.server.domain.point.entity.PointHistory;
import kr.hhplus.be.server.domain.point.entity.PointHistoryType;
import kr.hhplus.be.server.domain.point.repository.PointHistoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointHistoryServiceTest {

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    @InjectMocks
    private PointHistoryService pointHistoryService;

    @Test
    @DisplayName("유효한 인자들로 포인트 내역 생성을 요청하면 생성된 PointHistory를 반환한다")
    void createPointHistory_ShouldCreateAndSavePointHistory_WhenValidInputsAreProvided() {
        // Arrange
        Long pointId = 1L;
        PointHistoryType type = PointHistoryType.RECHARGE;
        Long amount = 1000L;
        PointHistory pointHistory = new PointHistory(pointId, type, amount);

        when(pointHistoryRepository.save(any(PointHistory.class))).thenReturn(pointHistory);

        // Act
        PointHistory createdHistory = pointHistoryService.createPointHistory(pointId, type, amount);

        // Assert
        assertThat(createdHistory).isNotNull();
        assertThat(createdHistory.getPointId()).isEqualTo(pointId);
        assertThat(createdHistory.getType()).isEqualTo(type);
        assertThat(createdHistory.getAmount()).isEqualTo(amount);
        verify(pointHistoryRepository, times(1)).save(any(PointHistory.class));
    }

    @Test
    @DisplayName("포인트 아이디에 해당하는 포인트 내역이 있으면 PointHistory 리스트를 반환한다")
    void getPointHistoriesByPointId_ShouldReturnHistories_WhenPointIdExists() {
        // Arrange
        Long pointId = 1L;
        List<PointHistory> histories = Arrays.asList(
                new PointHistory(pointId, PointHistoryType.RECHARGE, 1000L),
                new PointHistory(pointId, PointHistoryType.DEDUCT, 500L)
        );

        when(pointHistoryRepository.findAllByPointId(pointId)).thenReturn(histories);

        // Act
        List<PointHistory> result = pointHistoryService.getPointHistoriesByPointId(pointId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getType()).isEqualTo(PointHistoryType.RECHARGE);
        assertThat(result.get(1).getType()).isEqualTo(PointHistoryType.DEDUCT);
        verify(pointHistoryRepository, times(1)).findAllByPointId(pointId);
    }

    @Test
    @DisplayName("포인트 내역이 없으면 빈배열을 반환한다")
    void getPointHistoriesByPointId_ShouldReturnEmptyList_WhenNoHistoriesExist() {
        // Arrange
        Long pointId = 1L;

        when(pointHistoryRepository.findAllByPointId(pointId)).thenReturn(List.of());

        // Act
        List<PointHistory> result = pointHistoryService.getPointHistoriesByPointId(pointId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(pointHistoryRepository, times(1)).findAllByPointId(pointId);
    }
}