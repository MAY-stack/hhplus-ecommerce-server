package kr.hhplus.be.server.infrastructure.user.repository;

import kr.hhplus.be.server.domain.user.entity.Users;
import kr.hhplus.be.server.domain.user.repository.UserRepository;
import kr.hhplus.be.server.infrastructure.user.jpa.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public boolean existsById(String userId) {
        return userJpaRepository.existsById(userId);
    }

    @Override
    public Users save(Users users) {
        return userJpaRepository.save(users);
    }

    @Override
    public Optional<Users> findById(String userId) {
        return userJpaRepository.findById(userId);
    }
}
