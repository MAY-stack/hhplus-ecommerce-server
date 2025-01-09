package kr.hhplus.be.server.domain.point.service;

import kr.hhplus.be.server.domain.point.entity.PointHistory;
import kr.hhplus.be.server.domain.point.repository.PointHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointHistoryServiceImpl implements PointHistoryService {
    private final PointHistoryRepository pointHistoryRepository;

    // 포인트 충전 성공 기록
    public void createRechargeSuccess(Long pointId, Long amount) {
        PointHistory pointHistory = PointHistory.createRechargeSuccess(pointId, amount);
        pointHistoryRepository.save(pointHistory);
    }

    // 포인트 충전 실패 기록
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createRechargeFailure(Long pointId, Long amount) {
        PointHistory pointHistory = PointHistory.createRechargeFailure(pointId, amount);
        pointHistoryRepository.save(pointHistory);
    }

    // 포인트 차감 성공 기록
    public void createDeductSuccess(Long pointId, Long amount) {
        PointHistory pointHistory = PointHistory.createDeductSuccess(pointId, amount);
        pointHistoryRepository.save(pointHistory);
    }

    // 포인트 차감 실패 기록
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createDeductFailure(Long pointId, Long amount) {
        PointHistory pointHistory = PointHistory.createDeductFailure(pointId, amount);
        pointHistoryRepository.save(pointHistory);
    }

}
