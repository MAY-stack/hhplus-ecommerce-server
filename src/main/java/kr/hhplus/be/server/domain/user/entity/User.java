package kr.hhplus.be.server.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.hhplus.be.server.common.exception.ErrorMessage;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
@Table(name = "Users")
public class User {
    @Id
    @Column(nullable = false, length = 50)
    private String id;

    @Column(nullable = false, length = 100)
    private String name;

    @CreationTimestamp
    private LocalDateTime createdAt;

    // 생성자
    public User(String id, String name) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException(ErrorMessage.USER_ID_REQUIRED.getMessage());
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(ErrorMessage.USER_NAME_REQUIRED.getMessage());
        }
        this.id = id;
        this.name = name;
    }
}
