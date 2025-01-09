package kr.hhplus.be.server.infrastructure.point.jpa;

import kr.hhplus.be.server.domain.point.entity.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PointJpaRepository extends JpaRepository<Point, Long> {
    Optional<Point> findByUserId(String userId);
}
