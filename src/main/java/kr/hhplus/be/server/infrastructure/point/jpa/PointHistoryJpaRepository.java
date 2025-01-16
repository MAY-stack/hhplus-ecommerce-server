package kr.hhplus.be.server.infrastructure.point.jpa;

import kr.hhplus.be.server.domain.point.entity.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointHistoryJpaRepository extends JpaRepository<PointHistory, Long> {

    List<PointHistory> findAllByPointId(Long pointId);

    void flush();
}
