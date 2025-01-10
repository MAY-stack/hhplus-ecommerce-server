package kr.hhplus.be.server.domain.user.service;

import kr.hhplus.be.server.domain.user.entity.Users;
import kr.hhplus.be.server.domain.user.exception.DuplicateUserIdException;
import kr.hhplus.be.server.domain.user.exception.UserNotFoundException;
import kr.hhplus.be.server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    // 사용자 생성
    @Transactional
    public Users createUser(String userId, String userName) {
        // 중복 확인
        if (userRepository.existsById(userId)) {
            throw new DuplicateUserIdException();
        }
        Users user = new Users(userId, userName);
        return userRepository.save(user);
    }

    // 사용자 조회

    public Users getUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }
}
