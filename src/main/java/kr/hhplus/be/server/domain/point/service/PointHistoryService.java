package kr.hhplus.be.server.domain.point.service;

import kr.hhplus.be.server.domain.point.entity.PointHistory;
import kr.hhplus.be.server.domain.point.entity.PointHistoryType;
import kr.hhplus.be.server.domain.point.repository.PointHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointHistoryService {
    private final PointHistoryRepository pointHistoryRepository;

    public PointHistory createPointHistory(Long pointId, PointHistoryType type, Long amount) {
        PointHistory pointHistory = new PointHistory(pointId, type, amount);
        return pointHistoryRepository.save(pointHistory);
    }
    
    public List<PointHistory> getPointHistoriesByPointId(Long pointId) {
        return pointHistoryRepository.findAllByPointId(pointId);
    }
}
