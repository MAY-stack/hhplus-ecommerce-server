package kr.hhplus.be.server.application.user;


import kr.hhplus.be.server.domain.point.service.PointService;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserFacade {
    private final UserService userService;
    private final PointService pointService;

    // 사용자 생성
    @Transactional
    public User createUser(String userId, String userName) {

        // 사용자 생성
        User user = userService.createUser(userId, userName);

        // 포인트 생성
        pointService.createPoint(userId);

        return user;
    }
}
