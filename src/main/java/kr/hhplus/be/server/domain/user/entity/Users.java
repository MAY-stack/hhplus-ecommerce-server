package kr.hhplus.be.server.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Users {
    @Id
    @Column(nullable = false, length = 50)
    private String id;

    @Column(nullable = false, length = 100)
    private String name;

    @CreationTimestamp
    private LocalDateTime createdAt;

    // 생성자
    public Users(String id, String name) {
        validate(id, "사용자 아이디는 필수입니다.");
        validate(name, "사용자 이름은 필수입니다.");

        this.id = id;
        this.name = name;
    }

    // 유효성 검증
    private void validate(Object value, String errorMessage) {
        if (value == null || value.toString().trim().isEmpty()) {
            throw new IllegalArgumentException(errorMessage);
        }
    }
}
