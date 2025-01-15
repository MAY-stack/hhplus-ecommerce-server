package kr.hhplus.be.server.application.coupon.facade;

import kr.hhplus.be.server.application.coupon.dto.CouponIssuanceInfoDto;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.entity.CouponIssuance;
import kr.hhplus.be.server.domain.coupon.exception.CouponNotFoundException;
import kr.hhplus.be.server.domain.coupon.repository.CouponIssuanceRepository;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.server.domain.coupon.service.CouponIssuanceService;
import kr.hhplus.be.server.domain.coupon.service.CouponService;
import kr.hhplus.be.server.domain.user.entity.Users;
import kr.hhplus.be.server.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponFacade {
    private final UserService userService;
    private final CouponService couponService;
    private final CouponIssuanceService couponIssuanceService;
    private final CouponIssuanceRepository couponIssuanceRepository;
    private final CouponRepository couponRepository;

    private final Logger log = LoggerFactory.getLogger(CouponFacade.class);

    @Transactional
    public CouponIssuanceInfoDto issueCoupon(Long couponId, String userId) {
        // 사용자 조회
        Users user = userService.getUserById(userId);

        // 남은 수량 감소 및 저장
        Coupon coupon = couponService.decreaseRemaining(couponId);

        // 쿠폰 발급
        CouponIssuance couponIssuance = couponIssuanceService.issueCoupon(coupon, user);

        return CouponIssuanceInfoDto.fromEntity(couponIssuance, coupon);
    }

    // 보유 쿠폰 목록 조회
    public List<CouponIssuanceInfoDto> getIssuedCouponList(String userId) {
        List<CouponIssuance> couponIssuanceList = couponIssuanceRepository.findAllByUserId(userId);

        return couponIssuanceList.stream()
                .map(couponIssuance -> {
                    Coupon coupon = couponRepository.findById(couponIssuance.getCouponId())
                            .orElseThrow(CouponNotFoundException::new);
                    return CouponIssuanceInfoDto.fromEntity(couponIssuance, coupon);
                })
                .toList();
    }
}
