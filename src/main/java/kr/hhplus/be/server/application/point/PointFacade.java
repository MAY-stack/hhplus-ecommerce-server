package kr.hhplus.be.server.application.point;


import kr.hhplus.be.server.domain.point.entity.Point;
import kr.hhplus.be.server.domain.point.entity.PointHistoryType;
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
        // 포인트 충전
        pointService.rechargePointWithLock(userId, amount);
        // 충전 기록
        pointHistoryService.createPointHistory(point.getId(), PointHistoryType.RECHARGE, amount);
        return point;
    }

}
