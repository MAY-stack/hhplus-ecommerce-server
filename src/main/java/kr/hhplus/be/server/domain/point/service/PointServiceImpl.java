package kr.hhplus.be.server.domain.point.service;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.point.entity.Point;
import kr.hhplus.be.server.domain.point.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {
    private final PointRepository pointRepository;

    // 포인트 생성
    @Override
    public Point createPoint(String userId) {
        return pointRepository.save(new Point(userId));
    }

    // 포인트 조회
    @Override
    public Point getPointByUserId(String userId) {
        return pointRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("포인트 정보를 찾을 수 없습니다."));
    }

    // 포인트 충전
    @Override
    @Transactional
    public Point rechargePoint(String userId, Long amount) {
        Point point = getPointByUserId(userId);
        point.addPoints(amount);
        return pointRepository.save(point);
    }

    @Override
    @Transactional
    public Point deductPoint(String userId, Long amount) {
        Point point = getPointByUserId(userId);
        point.usePoints(amount);
        return pointRepository.save(point);
    }

}
