package kr.hhplus.be.server.domain.user.service;

import kr.hhplus.be.server.common.exception.ErrorMessage;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    // 사용자 생성
    @Transactional
    public User createUser(String userId, String userName) {
        // 중복 확인
        if (userRepository.existsById(userId)) {
            throw new IllegalArgumentException(ErrorMessage.USER_ID_ALREADY_EXISTS.getMessage());
        }
        // 사용자 생성
        User user = new User(userId, userName);
        return userRepository.save(user);
    }

    // 사용자 조회
    public User getUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.USER_NOT_FOUND.getMessage()));
    }
}
