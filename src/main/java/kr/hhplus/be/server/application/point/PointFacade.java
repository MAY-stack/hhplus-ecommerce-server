package kr.hhplus.be.server.application.point;


import kr.hhplus.be.server.domain.point.entity.Point;
import kr.hhplus.be.server.domain.point.service.PointHistoryService;
import kr.hhplus.be.server.domain.point.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointFacade {
    private final PointService pointService;
    private final PointHistoryService pointHistoryService;

    // 포인트 충전 서비스
    @Transactional
    public Point rechargePointWithHistory(String userId, Long amount) {
        Point point = pointService.getPointByUserId(userId);
        try {
            // 포인트 충전
            pointService.rechargePoint(userId, amount);
            // 충전 성공 기록
            pointHistoryService.createRechargeSuccess(point.getId(), amount);
            return point;
        } catch (Exception e) {
            // 충전 실패 기록
            pointHistoryService.createRechargeFailure(point.getId(), amount);
            // 예외 다시 던지기
            throw new RuntimeException("포인트 충전에 실패했습니다: " + e.getMessage(), e);
        }
    }

    // 포인트 사용 서비스
    @Transactional
    public Point deductPointWithHistory(String userId, Long amount) {
        Point point = pointService.getPointByUserId(userId);
        try {
            // 포인트 충전
            pointService.deductPoint(userId, amount);
            // 충전 성공 기록
            pointHistoryService.createDeductSuccess(point.getId(), amount);
            return point;
        } catch (Exception e) {
            // 충전 실패 기록
            pointHistoryService.createDeductFailure(point.getId(), amount);
            // 예외 다시 던지기
            throw new RuntimeException("포인트 사용에 실패했습니다: " + e.getMessage(), e);
        }
    }
}
