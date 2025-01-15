package kr.hhplus.be.server.domain.coupon.service;

import kr.hhplus.be.server.common.exception.ErrorMessage;
import kr.hhplus.be.server.domain.coupon.entity.CouponIssuance;
import kr.hhplus.be.server.domain.coupon.entity.IssuedCouponStatus;
import kr.hhplus.be.server.domain.coupon.repository.CouponIssuanceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponIssuanceServiceTest {

    @Mock
    private CouponIssuanceRepository couponIssuanceRepository;

    @InjectMocks
    private CouponIssuanceService couponIssuanceService;

    @Test
    @DisplayName("쿠폰 아이디와 사용자 아이디로 쿠폰 발급을 요청하면 발급된 CouponIssuance 객체를 반환한다")
    void issueCoupon_ShouldReturnSavedCouponIssuance() {
        // Arrange
        Long couponId = 1L;
        String userId = "user123";
        CouponIssuance couponIssuance = new CouponIssuance(couponId, userId);

        when(couponIssuanceRepository.existsByCouponIdAndUserId(couponId, userId)).thenReturn(false);
        when(couponIssuanceRepository.save(any(CouponIssuance.class))).thenReturn(couponIssuance);

        // Act
        CouponIssuance issuedCoupon = couponIssuanceService.issueCoupon(couponId, userId);

        // Assert
        assertThat(issuedCoupon).isEqualTo(couponIssuance);
        verify(couponIssuanceRepository, times(1)).existsByCouponIdAndUserId(couponId, userId);
        verify(couponIssuanceRepository, times(1)).save(any(CouponIssuance.class));
    }

    @Test
    @DisplayName("같은 사용자가 이미 발급받은 쿠폰에 대해 발급 요청하면 IllegalArgumentException을 던진다")
    void issueCoupon_ShouldThrowException_WhenCouponAlreadyIssued() {
        // Arrange
        Long couponId = 1L;
        String userId = "user123";

        when(couponIssuanceRepository.existsByCouponIdAndUserId(couponId, userId)).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> couponIssuanceService.issueCoupon(couponId, userId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.COUPON_ALREADY_ISSUED.getMessage());
        verify(couponIssuanceRepository, times(1)).existsByCouponIdAndUserId(couponId, userId);
    }

    @Test
    @DisplayName("발급쿠폰 아이디로 조회하면 아이디에 해당하는 CouponIssuance객체를 반환한다")
    void getCouponIssuanceById_ShouldReturnCouponIssuance() {
        // Arrange
        String issuanceId = "issuance123";
        CouponIssuance couponIssuance = mock(CouponIssuance.class);

        when(couponIssuanceRepository.findById(issuanceId)).thenReturn(Optional.of(couponIssuance));

        // Act
        CouponIssuance foundCouponIssuance = couponIssuanceService.getCouponIssuanceById(issuanceId);

        // Assert
        assertThat(foundCouponIssuance).isEqualTo(couponIssuance);
        verify(couponIssuanceRepository, times(1)).findById(issuanceId);
    }

    @Test
    @DisplayName("아이디에 해당하는 발급쿠폰이 없으면 IllegalArgumentException를 던진다")
    void getCouponIssuanceById_ShouldThrowException_WhenNotFound() {
        // Arrange
        String issuanceId = "issuance123";

        when(couponIssuanceRepository.findById(issuanceId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> couponIssuanceService.getCouponIssuanceById(issuanceId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.COUPON_NOT_FOUND.getMessage());
        verify(couponIssuanceRepository, times(1)).findById(issuanceId);
    }

    @Test
    @DisplayName("CouponIssuance 객체와 사용자 아이디로 쿠폰 사용처리를 하면 사용한 CouponIssuance를 반환한다")
    void useIssuedCoupon_ShouldUpdateStatusToUsed() {
        // Arrange
        CouponIssuance couponIssuance = mock(CouponIssuance.class);
        String userId = "user123";

        when(couponIssuanceRepository.save(any(CouponIssuance.class))).thenReturn(couponIssuance);

        // Act
        CouponIssuance updatedCouponIssuance = couponIssuanceService.useIssuedCoupon(couponIssuance, userId);

        // Assert
        verify(couponIssuance, times(1)).changeStatusToUsed(userId);
        assertThat(updatedCouponIssuance).isEqualTo(couponIssuance);
        verify(couponIssuanceRepository, times(1)).save(couponIssuance);
    }

    @Test
    @DisplayName("쿠폰 아이디에 해당하는 발급쿠폰 중 사용하지 않은 CouponIssuance 리스트를 조회할 수 있다")
    void getUnusedCouponByCouponId_ShouldReturnUnusedCoupons() {
        // Arrange
        Long couponId = 1L;
        List<CouponIssuance> unusedCoupons = Arrays.asList(
                new CouponIssuance(couponId, "user123"),
                new CouponIssuance(couponId, "user456")
        );

        when(couponIssuanceRepository.findByCouponIdAndStatus(couponId, IssuedCouponStatus.ISSUED)).thenReturn(unusedCoupons);

        // Act
        List<CouponIssuance> foundUnusedCoupons = couponIssuanceService.getUnusedCouponByCouponId(couponId);

        // Assert
        assertThat(foundUnusedCoupons).isEqualTo(unusedCoupons);
        verify(couponIssuanceRepository, times(1)).findByCouponIdAndStatus(couponId, IssuedCouponStatus.ISSUED);
    }

    @Test
    @DisplayName("사용자 아이디로 발급된 CouponIssuance 리스트를 조회할 수 있다")
    void getCouponIssuanceByUserId_ShouldReturnUserCoupons() {
        // Arrange
        String userId = "user123";
        List<CouponIssuance> userCoupons = Arrays.asList(
                new CouponIssuance(1L, userId),
                new CouponIssuance(2L, userId)
        );

        when(couponIssuanceRepository.findAllByUserId(userId)).thenReturn(userCoupons);

        // Act
        List<CouponIssuance> foundUserCoupons = couponIssuanceService.getCouponIssuanceByUserId(userId);

        // Assert
        assertThat(foundUserCoupons).isEqualTo(userCoupons);
        verify(couponIssuanceRepository, times(1)).findAllByUserId(userId);
    }

    @Test
    @DisplayName("쿠폰의 상태를 Expire로 설정하고 저장한다")
    void expireUnusedCoupon_ShouldUpdateStatusToExpiredAndSave() {
        // Arrange
        CouponIssuance couponIssuance = mock(CouponIssuance.class);
        doNothing().when(couponIssuance).changeStatusExpire();

        // Act
        couponIssuanceService.expireUnusedCoupon(couponIssuance);

        // Assert
        verify(couponIssuance).changeStatusExpire();
        verify(couponIssuanceRepository).save(couponIssuance);
    }
}
