package kr.hhplus.be.server.domain.user.repository;

import kr.hhplus.be.server.domain.user.entity.Users;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository {
    boolean existsById(String userId);

    Users save(Users users);

    Optional<Users> findById(String userId);
}
