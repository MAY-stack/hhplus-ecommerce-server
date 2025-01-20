package kr.hhplus.be.server.domain.point.repository;

import kr.hhplus.be.server.domain.point.entity.Point;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PointRepository {
    Point save(Point point);

    Optional<Point> findByUserId(String userId);

    Optional<Point> findByUserIdWithLock(String userId);

    void deleteAllInBatch();
}
