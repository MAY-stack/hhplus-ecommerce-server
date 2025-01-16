package kr.hhplus.be.server.domain.user.service;

import kr.hhplus.be.server.common.exception.ErrorMessage;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("존재하지 않는 사용자 아이디로 사용자를 생성하면 생성된 User 객체를 반환한다")
    void createUser_ShouldCreateAndReturnUser_WhenUserIdDoesNotExist() {
        // Arrange
        String userId = "user123";
        String userName = "Test User";
        User user = new User(userId, userName);

        when(userRepository.existsById(userId)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User createdUser = userService.createUser(userId, userName);

        // Assert
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getId()).isEqualTo(userId);
        assertThat(createdUser.getName()).isEqualTo(userName);
        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("이미 존재하는 사용자 아이디로 사용자를 생성하면 IllegalArgumentException을 던진다")
    void createUser_ShouldThrowException_WhenUserIdAlreadyExists() {
        // Arrange
        String userId = "user123";
        String userName = "Test User";

        when(userRepository.existsById(userId)).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userService.createUser(userId, userName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.USER_ID_ALREADY_EXISTS.getMessage());
        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("존재하는 사용자의 id로 사용자를 조회하면 id에 해당하는 User 객체를 반환한다")
    void getUserById_ShouldReturnUser_WhenUserExists() {
        // Arrange
        String userId = "user123";
        User user = new User(userId, "Test User");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        User foundUser = userService.getUserById(userId);

        // Assert
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(userId);
        assertThat(foundUser.getName()).isEqualTo("Test User");
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("존재하지 않는 사용자 id로 조회하면 IllegalArgumentException을 던진다")
    void getUserById_ShouldThrowException_WhenUserDoesNotExist() {
        // Arrange
        String userId = "user123";

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.getUserById(userId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.USER_NOT_FOUND.getMessage());
        verify(userRepository, times(1)).findById(userId);
    }
}

