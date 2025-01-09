package kr.hhplus.be.server.domain.user.service;

import kr.hhplus.be.server.domain.user.entity.Users;
import kr.hhplus.be.server.domain.user.exception.DuplicateUserIdException;
import kr.hhplus.be.server.domain.user.exception.UserNotFoundException;
import kr.hhplus.be.server.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void 사용자_아이디와_사용자_이름으로_사용자를_생성하면_생성된_Users객체를_반환한다() {
        // Given
        String userId = "user123";
        String userName = "사용자123";

        when(userRepository.existsById(userId)).thenReturn(false);
        when(userRepository.save(any(Users.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Users createdUser = userService.createUser(userId, userName);

        // Then
        assertAll(
                () -> assertNotNull(createdUser),
                () -> assertEquals(userId, createdUser.getId()),
                () -> assertEquals(userName, createdUser.getName())
        );
        verify(userRepository).existsById(userId);
        verify(userRepository).save(any(Users.class));
    }

    @Test
    void 중복된_사용자_아이디로_사용자를_생성하면_DuplicateUserIdException을_던진다() {
        // Given
        String userId = "user123";
        String userName = "사용자123";

        when(userRepository.existsById(userId)).thenReturn(true);

        // When
        Exception exception = assertThrows(DuplicateUserIdException.class,
                () -> userService.createUser(userId, userName));

        // Then
        assertEquals("이미 존재하는 사용자 ID입니다.", exception.getMessage());

        verify(userRepository).existsById(userId);
    }

    @Test
    void 사용자_아이디나_사용자이름이_null_이면_IllegalException을_던진다() {
        // Given
        String userId = "user123";
        String userName = "사용자123";

        when(userRepository.existsById(null)).thenReturn(false);
        when(userRepository.existsById(userId)).thenReturn(false);

        // When
        Exception exceptionId = assertThrows(IllegalArgumentException.class,
                () -> userService.createUser(null, userName));
        Exception exceptionName = assertThrows(IllegalArgumentException.class,
                () -> userService.createUser(userId, null));

        // Then
        assertAll(
                () -> assertEquals("사용자 아이디는 필수입니다.", exceptionId.getMessage()),
                () -> assertEquals("사용자 이름은 필수입니다.", exceptionName.getMessage())
        );

        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, times(1)).existsById(null);
    }

    @Test
    void 사용자_아이디로_사용자를_찾으면_Users_객체를_반환한다() {
        // Given
        String userId = "user123";
        String userName = "사용자123";
        Users users = new Users(userId, userName);

        // Mock 동작 정의
        when(userRepository.findById(userId)).thenReturn(Optional.of(users));

        // When
        Users result = userService.getUserById(userId);

        // Then
        assertAll(
                "사용자 정보 검증",
                () -> assertNotNull(result, "결과 객체가 null이 아닙니다."),
                () -> assertEquals(userId, result.getId(), "ID가 일치하지 않습니다."),
                () -> assertEquals(userName, result.getName(), "이름이 일치하지 않습니다.")
        );

        // Mock 호출 검증
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void 존재하지_않는_사용자_아이디로_조회하면_UserNotFoundException을_던진다() {
        // Given
        String userId = "nonExistentUser";

        // Mock 동작 정의: 해당 사용자 ID로 조회 시 빈 값 반환
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.getUserById(userId),
                "사용자 정보를 찾을 수 없습니다."
        );

        //  Then
        assertEquals("사용자 정보를 찾을 수 없습니다.", exception.getMessage());

        // Mock 호출 검증
        verify(userRepository, times(1)).findById(userId);
    }

}
