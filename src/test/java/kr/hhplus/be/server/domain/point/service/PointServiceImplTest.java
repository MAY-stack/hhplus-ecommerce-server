package kr.hhplus.be.server.domain.point.service;

import kr.hhplus.be.server.domain.point.entity.Point;
import kr.hhplus.be.server.domain.point.repository.PointRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PointServiceImplTest {

    @Mock
    private PointRepository pointRepository;

    @InjectMocks
    private PointServiceImpl pointService;

    @Test
    void 사용자_아이디로_포인트생성을_요청하면_생성된_Point_객체를_반환한다() {
        // Given
        String userId = "user123";
        Point point = new Point(userId);

        when(pointRepository.save(any(Point.class))).thenReturn(point);

        // When
        Point createdPoint = pointService.createPoint(userId);

        // Then
        assertAll(
                () -> assertNotNull(createdPoint),
                () -> assertEquals(userId, createdPoint.getUserId()),
                () -> assertEquals(0L, createdPoint.getBalance())
        );


        verify(pointRepository, times(1)).save(any(Point.class));
    }

    @Test
    void 사용자_아이디가_null이면_IllegalArgumentException을_던진다() {
        // Given
        String userId = null;

        // When
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> pointService.createPoint(userId)
        );

        // Then
        assertEquals("사용자 아이디는 필수입니다.", exception.getMessage());
        verify(pointRepository, never()).save(any(Point.class));
    }

    @Test
    void 한번에_1이상_100만미만포인를_잔액1000만포인트_이내에서_충전하면_충전된_포인트를_반환한다() {

        // Given
        String userId = "user123";
        Long initialBalance = 9000000L; // 초기 잔액
        Long amountToRecharge = 500000L; // 충전 금액
        Point point = new Point(userId, initialBalance);

        // Mock 동작 정의
        when(pointRepository.findByUserId(userId)).thenReturn(Optional.of(point));
        when(pointRepository.save(any(Point.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Point updatedPoint = pointService.rechargePoint(userId, amountToRecharge);

        // Then
        assertAll(
                "포인트 충전 결과 검증",
                () -> assertNotNull(updatedPoint, "충전된 포인트 객체가 null이어서는 안 됩니다."),
                () -> assertEquals(userId, updatedPoint.getUserId(), "사용자 ID가 일치하지 않습니다."),
                () -> assertEquals(initialBalance + amountToRecharge, updatedPoint.getBalance(), "충전 후 잔액이 올바르지 않습니다.")
        );

        verify(pointRepository, times(1)).findByUserId(userId);
        verify(pointRepository, times(1)).save(any(Point.class));
    }

    @Test
    void 충전금액이_0미만이면_IllegalArgumentException을_던진다() {
        // Given
        String userId = "user123";
        Long invalidAmount = -1000L; // 0 미만 충전 금액
        Point point = new Point(userId, 5000L); // 초기 잔액 5000 포인트

        // Mock 동작 정의
        when(pointRepository.findByUserId(userId)).thenReturn(Optional.of(point));

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> pointService.rechargePoint(userId, invalidAmount)
        );

        assertEquals("충전 금액은 0보다 커야 합니다. 입력된 값: -1000", exception.getMessage());

        // Mock 호출 검증
        verify(pointRepository, times(1)).findByUserId(userId);
        verify(pointRepository, never()).save(any(Point.class)); // 저장 호출이 없어야 함
    }

    @Test
    void 충전금액이_100만포인트이상이면_IllegalArgumentException을_던진다() {
        // Given
        String userId = "user123";
        Long invalidAmount = 1_500_000L; // 100만 초과 충전 금액
        Point point = new Point(userId, 5_000_000L); // 초기 잔액 500만 포인트

        // Mock 동작 정의
        when(pointRepository.findByUserId(userId)).thenReturn(Optional.of(point));

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> pointService.rechargePoint(userId, invalidAmount)
        );

        assertEquals("1회 최대 충전 금액은 1000000 포인트를 초과할 수 없습니다. 입력된 값: 1500000", exception.getMessage());

        // Mock 호출 검증
        verify(pointRepository, times(1)).findByUserId(userId);
        verify(pointRepository, never()).save(any(Point.class)); // 저장 호출이 없어야 함
    }

    @Test
    void 충전후_잔액이_1000만포인트이상이면_예외발생() {
        // Given
        String userId = "user123";
        Long initialBalance = 9_500_000L; // 초기 잔액
        Long amountToRecharge = 1_000_000L; // 충전 금액
        Point point = new Point(userId, initialBalance);

        // Mock 동작 정의
        when(pointRepository.findByUserId(userId)).thenReturn(Optional.of(point));

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> pointService.rechargePoint(userId, amountToRecharge)
        );

        assertEquals(
                "최대 보유 잔고는 10000000 포인트 미만입니다. 현재 잔고: 9500000, 입력된 충전 금액: 1000000",
                exception.getMessage()
        );

        // Mock 호출 검증
        verify(pointRepository, times(1)).findByUserId(userId);
        verify(pointRepository, never()).save(any(Point.class)); // 저장 호출이 없어야 함
    }


}
