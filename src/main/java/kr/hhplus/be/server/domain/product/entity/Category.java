package kr.hhplus.be.server.domain.product.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.common.exception.ErrorMessage;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    public Category(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(ErrorMessage.CATEGORY_NAME_REQUIRED.getMessage());
        }
        this.name = name;
    }
}
