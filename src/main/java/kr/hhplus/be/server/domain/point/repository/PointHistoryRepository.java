package kr.hhplus.be.server.domain.point.repository;

import kr.hhplus.be.server.domain.point.entity.PointHistory;
import org.springframework.stereotype.Repository;

@Repository
public interface PointHistoryRepository {
    PointHistory save(PointHistory pointHistory);
}
