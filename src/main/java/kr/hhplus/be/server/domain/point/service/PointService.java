package kr.hhplus.be.server.domain.point.service;

import kr.hhplus.be.server.domain.point.entity.Point;
import org.springframework.stereotype.Service;

@Service
public interface PointService {

    // 포인트 생성
    Point createPoint(String userId);

    // 포인트 조회
    Point getPointByUserId(String userId);

    // 포인트 충전
    Point rechargePoint(String userId, Long amount);

    // 포인트 사용
    Point deductPoint(String userId, Long amount);
}
