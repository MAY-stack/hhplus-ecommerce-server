package kr.hhplus.be.server.domain.user.service;

import kr.hhplus.be.server.domain.user.entity.Users;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    // 사용자 생성
    public Users createUser(String userId, String userName);

    // 사용자 조회
    public Users getUserById(String userId);
}
