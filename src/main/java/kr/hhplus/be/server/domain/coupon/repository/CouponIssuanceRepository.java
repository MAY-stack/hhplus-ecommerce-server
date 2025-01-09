package kr.hhplus.be.server.domain.coupon.repository;

import kr.hhplus.be.server.domain.coupon.entity.CouponIssuance;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CouponIssuanceRepository {
    boolean existsByCouponIdAndUserId(Long couponId, String userId);

    CouponIssuance save(CouponIssuance couponIssuance);

    Optional<CouponIssuance> findById(String id);

    List<CouponIssuance> findAllByUserId(String userId);
}
