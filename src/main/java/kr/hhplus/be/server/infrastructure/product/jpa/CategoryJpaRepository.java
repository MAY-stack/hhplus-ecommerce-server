package kr.hhplus.be.server.infrastructure.product.jpa;

import kr.hhplus.be.server.domain.product.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryJpaRepository extends JpaRepository<Category, Long> {
}
