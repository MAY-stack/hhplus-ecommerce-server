package kr.hhplus.be.server.domain.point.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public interface PointHistoryService {

    // 포인트 충전 성공 기록
    void createRechargeSuccess(Long pointId, Long amount);

    // 포인트 충전 실패 기록
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void createRechargeFailure(Long pointId, Long amount);

    // 포인트 차감 성공 기록
    void createDeductSuccess(Long pointId, Long amount);

    // 포인트 차감 실패 기록
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void createDeductFailure(Long pointId, Long amount);

}
