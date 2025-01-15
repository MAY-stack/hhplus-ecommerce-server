package kr.hhplus.be.server.infrastructure.point.jpa;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.point.entity.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PointJpaRepository extends JpaRepository<Point, Long> {

    Optional<Point> findByUserId(String userId);

    // 특정 사용자 ID로 Point 엔티티 조회
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Point p WHERE p.userId = :userId")
    Optional<Point> findByUserIdWithLock(String userId);
}
