package kr.hhplus.be.server.domain.user.repository;

import kr.hhplus.be.server.domain.user.entity.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository {
    boolean existsById(String userId);

    Optional<User> findById(String userId);

    User save(User user);
}
