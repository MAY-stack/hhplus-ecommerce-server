package kr.hhplus.be.server.domain.product.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.common.exception.ErrorMessage;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false)
    private Long price;

    @Column(nullable = false)
    private Integer stock;

    @Column
    private Long categoryId;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Product(String name, Long price, Integer stock, Long categoryId) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(ErrorMessage.PRODUCT_NAME_REQUIRED.getMessage());
        }
        if (stock <= 0) {
            throw new IllegalArgumentException(ErrorMessage.MINIMUM_STOCK_VIOLATION.getMessage());
        }
        if (price <= 0) {
            throw new IllegalArgumentException(ErrorMessage.MINIMUM_PRICE_VIOLATION.getMessage());
        }
        if (categoryId == null) {
            throw new IllegalArgumentException(ErrorMessage.CATEGORY_ID_REQUIRED.getMessage());
        }
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.categoryId = categoryId;
    }

    // 재고 확인 및 감소 메서드
    public void reduceStock(Integer quantity) {
        if (this.stock < quantity) {
            throw new IllegalArgumentException(ErrorMessage.PRODUCT_SOLD_OUT.getMessage());
        }
        this.stock -= quantity;
    }

}