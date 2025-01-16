package kr.hhplus.be.server.domain.point.service;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.common.exception.ErrorMessage;
import kr.hhplus.be.server.domain.point.entity.Point;
import kr.hhplus.be.server.domain.point.repository.PointHistoryRepository;
import kr.hhplus.be.server.domain.point.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointService {
    private final PointRepository pointRepository;
    private final PointHistoryRepository pointHistoryRepository;

    // 포인트 생성
    public Point createPoint(String userId) {
        Point point = new Point(userId, 0L);
        return pointRepository.save(point);
    }

    // 포인트 조회
    public Point getPointByUserId(String userId) {
        return pointRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.POINT_NOT_FOUND.getMessage()));
    }

    // 포인트 충전
    @Transactional
    public Point rechargePoint(String userId, Long amount) {
        Point point = pointRepository.findByUserIdWithLock(userId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.POINT_NOT_FOUND.getMessage()));
        point.addPoints(amount);
        return pointRepository.save(point);
    }

    // 포인트 사용
    @Transactional
    public Point deductPoint(String userId, Long amount) {
        Point point = pointRepository.findByUserIdWithLock(userId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.POINT_NOT_FOUND.getMessage()));
        point.usePoints(amount);
        return pointRepository.save(point);
    }
}
