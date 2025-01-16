package kr.hhplus.be.server.application.user;

import kr.hhplus.be.server.domain.point.entity.Point;
import kr.hhplus.be.server.domain.point.repository.PointRepository;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserFacadeIntegrationTest {

    @Autowired
    private UserFacade userFacade;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PointRepository pointRepository;

    @Test
    @DisplayName("사용자를 생성하면 사용자와 포인트를 생성 및 저장하고 생성된 User 객체를 반환한다")
    void createUser_ShouldCreateUserAndPoint() {
        // Arrange
        String userId = "testUser";
        String userName = "Test User";

        // Act
        User createdUser = userFacade.createUser(userId, userName);

        // Assert: 사용자 검증
        User savedUser = userRepository.findById(userId).orElse(null);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isEqualTo(userId);
        assertThat(savedUser.getName()).isEqualTo(userName);

        // Assert: 포인트 검증
        Point savedPoint = pointRepository.findByUserId(userId).orElse(null);
        assertThat(savedPoint).isNotNull();
        assertThat(savedPoint.getUserId()).isEqualTo(userId);
        assertThat(savedPoint.getBalance()).isEqualTo(0L); // 초기 잔액은 0
    }
}
