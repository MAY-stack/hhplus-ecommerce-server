package kr.hhplus.be.server.infrastructure.coupon.jpa;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CouponJpaRepository extends JpaRepository<Coupon, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Coupon c WHERE c.id = :id")
    Optional<Coupon> findCouponByIdWithLock(@Param("id") Long id);

    List<Coupon> findAllByExpirationDate(LocalDate expirationDate);

    @Query("SELECT c.remainingQuantity FROM Coupon c WHERE c.id = :couponId")
    int findRemainingQuantityById(@Param("couponId") Long couponId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Coupon c SET c.remainingQuantity = :remaining WHERE c.id = :couponId")
    int updateRemainingQuantity(@Param("couponId") Long couponId, @Param("remaining") int remaining);
}
