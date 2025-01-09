package kr.hhplus.be.server.domain.product.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.product.exception.InsufficientStockException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
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
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.categoryId = categoryId;
    }

    // 재고 확인 및 감소 메서드
    public void reduceStock(Integer quantity) {
        if (this.stock < quantity) {
            throw new InsufficientStockException("재고가 부족합니다. 현재 재고: " + this.stock);
        }
        this.stock -= quantity;
    }

}