package kr.hhplus.be.server.infrastructure.point.repository;

import kr.hhplus.be.server.domain.point.entity.Point;
import kr.hhplus.be.server.domain.point.repository.PointRepository;
import kr.hhplus.be.server.infrastructure.point.jpa.PointJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PointRepositoryImpl implements PointRepository {

    private final PointJpaRepository pointJpaRepository;

    @Override
    public Point save(Point point) {
        return pointJpaRepository.save(point);
    }

    @Override
    public Optional<Point> findByUserId(String userId) {
        return pointJpaRepository.findByUserId(userId);
    }

    @Override
    public Optional<Point> findByUserIdWithLock(String userId) {
        return pointJpaRepository.findByUserIdWithLock(userId);
    }

    @Override
    public void deleteAllInBatch() {
        pointJpaRepository.deleteAllInBatch();
    }
}
