package kr.hhplus.be.server.infrastructure.user.jpa;

import kr.hhplus.be.server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserJpaRepository extends JpaRepository<User, String> {
}
