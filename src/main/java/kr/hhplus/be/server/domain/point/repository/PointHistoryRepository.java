package kr.hhplus.be.server.domain.point.repository;

import kr.hhplus.be.server.domain.point.entity.PointHistory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointHistoryRepository {
    PointHistory save(PointHistory pointHistory);

    List<PointHistory> findAllByPointId(Long pointId);

    void deleteAllInBatch();
}
