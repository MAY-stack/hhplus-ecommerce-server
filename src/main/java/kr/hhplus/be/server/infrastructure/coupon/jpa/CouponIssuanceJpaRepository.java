package kr.hhplus.be.server.infrastructure.coupon.jpa;

import kr.hhplus.be.server.domain.coupon.entity.CouponIssuance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouponIssuanceJpaRepository extends JpaRepository<CouponIssuance, String> {

    @Query("SELECT ci FROM CouponIssuance ci JOIN FETCH Coupon c ON ci.couponId = c.id WHERE ci.userId = :userId")
    Page<CouponIssuance> findByUserIdWithCouponDetails(@Param("userId") String userId, Pageable pageable);

    boolean existsByCouponIdAndUserId(Long couponId, String userId);

    List<CouponIssuance> findAllByCouponId(Long expiredCouponId);

    List<CouponIssuance> findAllByUserId(String userId);
}
