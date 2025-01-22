package kr.hhplus.be.server.application.coupon.facade;

import kr.hhplus.be.server.application.coupon.dto.CouponIssuanceInfoDto;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.entity.CouponIssuance;
import kr.hhplus.be.server.domain.coupon.entity.IssuedCouponStatus;
import kr.hhplus.be.server.domain.coupon.repository.CouponIssuanceRepository;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CouponFacadeIntegrationTest {

    @Autowired
    private CouponFacade couponFacade;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CouponIssuanceRepository couponIssuanceRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("쿠폰 발급을 요청하면 쿠폰이 발급되고 쿠폰의 잔여수량을 감량한다")
    void issueCoupon_ShouldIssueCouponAndDecreaseRemaining() {
        // Arrange
        User user = new User("testUser", "Test User");
        userRepository.save(user);

        Coupon coupon = new Coupon("Test Coupon", 1000L, 5000L, 10, LocalDate.now().plusDays(1));
        couponRepository.save(coupon);

        // Act
        CouponIssuanceInfoDto issuedCoupon = couponFacade.issueCoupon(coupon.getId(), user.getId());

        // Assert
        assertThat(issuedCoupon).isNotNull();
        assertThat(issuedCoupon.couponId()).isEqualTo(coupon.getId());
        assertThat(issuedCoupon.userId()).isEqualTo(user.getId());

        Coupon updatedCoupon = couponRepository.findById(coupon.getId()).orElseThrow();
        assertThat(updatedCoupon.getRemainingQuantity()).isEqualTo(9); // Remaining quantity should decrease by 1
    }

    @Test
    @DisplayName("사용자에게 발급된 쿠폰에 대한 정보 리스트를 조회할 수 있다")
    void getIssuedCouponList_ShouldReturnIssuedCouponsForUser() {
        // Arrange
        User user = new User("testUser", "Test User");
        userRepository.save(user);

        Coupon coupon = new Coupon("Test Coupon", 1000L, 5000L, 10, LocalDate.now().plusDays(1));
        couponRepository.save(coupon);

        CouponIssuance couponIssuance = new CouponIssuance(coupon.getId(), user.getId());
        couponIssuanceRepository.save(couponIssuance);

        // Act
        List<CouponIssuanceInfoDto> issuedCoupons = couponFacade.getIssuedCouponList(user.getId());

        // Assert
        assertThat(issuedCoupons).hasSize(1);
        assertThat(issuedCoupons.get(0).couponId()).isEqualTo(coupon.getId());
        assertThat(issuedCoupons.get(0).userId()).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("사용하지 않은 쿠폰들을 만료상태로 변경할 수 있다")
    void expireUnusedCoupons_ShouldExpireEligibleCoupons() {
        // Arrange
        Coupon expiredCoupon = Coupon.builder()
                .title("Expired Coupon")
                .discountAmount(1000L)
                .minimumOrderAmount(5000L)
                .totalQuantity(10)
                .remainingQuantity(10)
                .expirationDate(LocalDate.now().minusDays(1))
                .build();

        couponRepository.save(expiredCoupon);

        CouponIssuance couponIssuance = new CouponIssuance(expiredCoupon.getId(), "testUser");
        couponIssuanceRepository.save(couponIssuance);

        // Act
        int expiredCount = couponFacade.expireUnusedCoupons();

        // Assert
        assertThat(expiredCount).isEqualTo(1);

        CouponIssuance updatedCouponIssuance = couponIssuanceRepository.findById(couponIssuance.getId()).orElseThrow();
        assertThat(updatedCouponIssuance.getStatus()).isEqualTo(IssuedCouponStatus.EXPIRED);
    }
}