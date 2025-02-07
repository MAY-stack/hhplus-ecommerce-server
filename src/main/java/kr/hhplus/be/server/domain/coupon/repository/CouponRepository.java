package kr.hhplus.be.server.domain.coupon.repository;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository {
    Optional<Coupon> findById(Long couponId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Coupon c WHERE c.id = :id")
    Optional<Coupon> findCouponByIdWithLock(@Param("id") Long id);

    List<Coupon> findAll();

    Coupon save(Coupon coupon);

    List<Coupon> findAllByExpirationDate(LocalDate currentDate);

    void deleteAllInBatch();

    int updateRemainingQuantity(Long couponId, int remaining);
}
