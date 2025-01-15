package kr.hhplus.be.server.domain.user.entity;

import kr.hhplus.be.server.common.exception.ErrorMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserTest {

    @Test
    @DisplayName("유효한 사용자 아이디와 사용자 이름으로 사용자를 생성할 수 있다.")
    void shouldCreateUserSuccessfullyWhenValidInputProvided() {
        // Arrange
        String validId = "user123";
        String validName = "John Doe";

        // Act
        User user = new User(validId, validName);

        // Assert
        assertThat(user.getId()).isEqualTo(validId);
        assertThat(user.getName()).isEqualTo(validName);
        assertThat(user.getCreatedAt()).isNull(); // CreatedAt is managed by Hibernate
    }

    @Test
    @DisplayName("id가 null이면 IllegalArgumentException을 던진다")
    void shouldThrowExceptionWhenIdIsNull() {
        // Arrange
        String invalidId = null;
        String validName = "John Doe";

        // Act & Assert
        assertThatThrownBy(() -> new User(null, validName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.USER_ID_REQUIRED.getMessage());
    }

    @Test
    @DisplayName("id 가 빈값이면 IllegalArgumentException을 던진다")
    void shouldThrowExceptionWhenIdIsBlank() {
        // Arrange
        String blankId = " ";
        String validName = "John Doe";

        // Act & Assert
        assertThatThrownBy(() -> new User(blankId, validName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.USER_ID_REQUIRED.getMessage());
    }

    @Test
    @DisplayName("이름이 null이면 IllegalArgumentException을 던진다")
    void shouldThrowExceptionWhenNameIsNull() {
        // Arrange
        String validId = "user123";
        String invalidName = null;

        // Act & Assert
        assertThatThrownBy(() -> new User(validId, invalidName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.USER_NAME_REQUIRED.getMessage());
    }

    @Test
    @DisplayName("이름이 빈값이면 IllegalArgumentException을 던진다")
    void shouldThrowExceptionWhenNameIsBlank() {
        // Arrange
        String validId = "user123";
        String blankName = " ";

        // Act & Assert
        assertThatThrownBy(() -> new User(validId, blankName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.USER_NAME_REQUIRED.getMessage());
    }
}
