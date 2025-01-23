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

    // 포인트 충전 (비관적 락)
    @Transactional
    public Point rechargePointWithLock(String userId, Long amount) {
        Point point = pointRepository.findByUserIdWithLock(userId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.POINT_NOT_FOUND.getMessage()));
        point.addPoints(amount);
        return pointRepository.save(point);
    }

    // 포인트 충전
    @Transactional
//    @Retryable(
//            retryFor = ObjectOptimisticLockingFailureException.class, // 낙관적 락 충돌 시 재시도
//            maxAttempts = 5,  // 최대 5번 재시도
//            backoff = @Backoff(delay = 100) // 100ms 대기 후 재시도
//    )
//    @DistributedLock(key = "update_point")
    public Point rechargePoint(String userId, Long amount) {
        Point point = pointRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.POINT_NOT_FOUND.getMessage()));
        point.addPoints(amount);
        return pointRepository.save(point);
    }

    // 포인트 사용 (비관적 락)
    @Transactional
    public Point deductPointWithLock(String userId, Long amount) {
        Point point = pointRepository.findByUserIdWithLock(userId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.POINT_NOT_FOUND.getMessage()));
        point.usePoints(amount);
        return pointRepository.save(point);
    }

    // 포인트 사용
    @Transactional
//    @Retryable(
//            retryFor = ObjectOptimisticLockingFailureException.class, // 낙관적 락 충돌 시 재시도
//            maxAttempts = 5,  // 최대 5번 재시도
//            backoff = @Backoff(delay = 100) // 100ms 대기 후 재시도
//    )
//    @DistributedLock(key = "update_point")
    public Point deductPoint(String userId, Long amount) {
        Point point = pointRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.POINT_NOT_FOUND.getMessage()));
        point.usePoints(amount);
        return pointRepository.save(point);
    }
}
