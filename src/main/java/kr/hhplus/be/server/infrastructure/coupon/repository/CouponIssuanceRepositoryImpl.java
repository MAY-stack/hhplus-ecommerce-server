package kr.hhplus.be.server.infrastructure.coupon.repository;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.coupon.entity.CouponIssuance;
import kr.hhplus.be.server.domain.coupon.entity.IssuedCouponStatus;
import kr.hhplus.be.server.domain.coupon.repository.CouponIssuanceRepository;
import kr.hhplus.be.server.infrastructure.coupon.jpa.CouponIssuanceJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CouponIssuanceRepositoryImpl implements CouponIssuanceRepository {

    private final CouponIssuanceJpaRepository couponIssuanceJpaRepository;

    @Override
    @Transactional
    public boolean existsByCouponIdAndUserId(Long couponId, String userId) {
        return couponIssuanceJpaRepository.existsByCouponIdAndUserId(couponId, userId);
    }

    @Override
    @Transactional
    public CouponIssuance save(CouponIssuance couponIssuance) {
        return couponIssuanceJpaRepository.save(couponIssuance);
    }

    @Override
    public Optional<CouponIssuance> findById(String id) {
        return couponIssuanceJpaRepository.findById(id);
    }

    @Override
    public List<CouponIssuance> findAllByUserId(String userId) {
        return couponIssuanceJpaRepository.findAllByUserId(userId);
    }

    @Override
    public List<CouponIssuance> findByCouponIdAndStatus(Long couponId, IssuedCouponStatus status) {
        return couponIssuanceJpaRepository.findAllByCouponIdAndStatus(couponId, status);
    }

    @Override
    public void deleteAllInBatch() {
        couponIssuanceJpaRepository.deleteAllInBatch();
    }
}
