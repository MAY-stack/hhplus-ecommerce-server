package kr.hhplus.be.server.domain.coupon.service;

import kr.hhplus.be.server.common.exception.ErrorMessage;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private CouponService couponService;

    @Test
    @DisplayName("쿠폰 생성을 요청하면 생성된 Coupon 객체를 반환한다")
    void createCoupon_ShouldReturnSavedCoupon() {
        // Arrange
        String title = "Test Coupon";
        Long discountAmount = 1000L;
        Long minimumOrderAmount = 5000L;
        Integer totalQuantity = 100;
        LocalDate expirationDate = LocalDate.now().plusDays(7);

        Coupon coupon = new Coupon(title, discountAmount, minimumOrderAmount, totalQuantity, expirationDate);
        when(couponRepository.save(any(Coupon.class))).thenReturn(coupon);

        // Act
        Coupon savedCoupon = couponService.createCoupon(title, discountAmount, minimumOrderAmount, totalQuantity, expirationDate);

        // Assert
        assertThat(savedCoupon).isEqualTo(coupon);
        verify(couponRepository, times(1)).save(any(Coupon.class));
    }

    @Test
    @DisplayName("쿠폰 아이디로 조회하면 아이디에 해당하는 Coupon 객체를 반환한다")
    void getCouponById_ShouldReturnCoupon() {
        // Arrange
        Long couponId = 1L;
        String title = "Test Coupon";
        Long discountAmount = 1000L;
        Long minimumOrderAmount = 5000L;
        Integer totalQuantity = 100;
        LocalDate expirationDate = LocalDate.now().plusDays(7);

        Coupon coupon = new Coupon(title, discountAmount, minimumOrderAmount, totalQuantity, expirationDate);
        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));

        // Act
        Coupon foundCoupon = couponService.getCouponById(couponId);

        // Assert
        assertThat(foundCoupon).isEqualTo(coupon);
        verify(couponRepository, times(1)).findById(couponId);
    }

    @Test
    @DisplayName("쿠폰 아이디에 해당하는 쿠폰이 없으면 IllegalArgumentException을 던진다")
    void getCouponById_ShouldThrowException_WhenCouponNotFound() {
        // Arrange
        Long couponId = 1L;
        when(couponRepository.findById(couponId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> couponService.getCouponById(couponId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.COUPON_NOT_FOUND.getMessage());
        verify(couponRepository, times(1)).findById(couponId);
    }

    @Test
    @DisplayName("잔여 수량이 있는 쿠폰의 수량 감량을 요청하면 수량을 감량한다")
    void decreaseRemaining_ShouldDecreaseQuantity() {
        // Arrange
        Long couponId = 1L;
        Coupon coupon = mock(Coupon.class);
        when(couponRepository.findCouponByIdWithLock(couponId)).thenReturn(Optional.of(coupon));
        when(couponRepository.save(any(Coupon.class))).thenReturn(coupon);

        // Act
        Coupon updatedCoupon = couponService.decreaseRemainingWithLock(couponId);

        // Assert
        verify(coupon).decreaseRemaining();
        assertThat(updatedCoupon).isEqualTo(coupon);
        verify(couponRepository, times(1)).findCouponByIdWithLock(couponId);
        verify(couponRepository, times(1)).save(coupon);
    }

    @Test
    @DisplayName("존재하지 않는 쿠폰의 수량 감량을 요청하면 IllegalArgumentException을 던진다")
    void decreaseRemaining_ShouldThrowException_WhenCouponNotFound() {
        // Arrange
        Long couponId = 1L;
        when(couponRepository.findCouponByIdWithLock(couponId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> couponService.decreaseRemainingWithLock(couponId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.COUPON_NOT_FOUND.getMessage());
        verify(couponRepository, times(1)).findCouponByIdWithLock(couponId);
    }

    @Test
    @DisplayName("만료된 쿠폰의 아이디들을 조회하면 어제가 만료일인 쿠폰의 아이디 리스트를 반환한다")
    void getAllExpiredCouponIds_ShouldReturnExpiredIdsForPreviousDay() {
        // Arrange
        LocalDate yesterday = LocalDate.now().minusDays(1);
        List<Coupon> expiredCoupons = Arrays.asList(
                Coupon.builder()
                        .title("Expired Coupon 1")
                        .discountAmount(1000L)
                        .minimumOrderAmount(5000L)
                        .totalQuantity(100)
                        .expirationDate(yesterday)
                        .build(),
                Coupon.builder()
                        .title("Expired Coupon 2")
                        .discountAmount(2000L)
                        .minimumOrderAmount(10000L)
                        .totalQuantity(50)
                        .expirationDate(yesterday)
                        .build()
        );
        when(couponRepository.findAllByExpirationDate(yesterday)).thenReturn(expiredCoupons);

        // Act
        List<Long> expiredCouponIds = couponService.getAllExpiredCouponIds();

        // Assert
        assertThat(expiredCouponIds).containsExactlyInAnyOrder(
                expiredCoupons.get(0).getId(),
                expiredCoupons.get(1).getId()
        );
        verify(couponRepository, times(1)).findAllByExpirationDate(yesterday);
    }

    @Test
    @DisplayName("어제가 만료일인 쿠폰이 없으면 빈 리스트를 반환한다")
    void getAllExpiredCouponIds_ShouldReturnEmptyList_WhenNoCouponsExpired() {
        // Arrange
        LocalDate yesterday = LocalDate.now().minusDays(1);
        when(couponRepository.findAllByExpirationDate(yesterday)).thenReturn(List.of());

        // Act
        List<Long> expiredCouponIds = couponService.getAllExpiredCouponIds();

        // Assert
        assertThat(expiredCouponIds).isEmpty();
        verify(couponRepository, times(1)).findAllByExpirationDate(yesterday);
    }

    @Test
    @DisplayName("조건에 맞는 쿠폰 적용을 요청하면 쿠폰 금액을 적용한 금액을 반환한다")
    void applyCoupon_ShouldReturnDiscountedAmount() {
        // Arrange
        Long couponId = 1L;
        Long totalAmount = 6000L;
        Coupon coupon = new Coupon("Discount Coupon", 1000L, 5000L, 100, LocalDate.now().plusDays(7));
        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));

        // Act
        Long discountedAmount = couponService.applyCoupon(totalAmount, couponId);

        // Assert
        assertThat(discountedAmount).isEqualTo(5000L);
        verify(couponRepository, times(1)).findById(couponId);
    }

    @Test
    @DisplayName("최소 주문금액 미만의 주문금액에 쿠폰 적용을 요청하면 IllegalArgumentException을 던진다")
    void applyCoupon_ShouldThrowException_WhenTotalAmountBelowMinimumOrderAmount() {
        // Arrange
        Long couponId = 1L;
        Long totalAmount = 4000L; // Minimum order amount is 5000
        Coupon coupon = new Coupon("Discount Coupon", 1000L, 5000L, 100, LocalDate.now().plusDays(7));
        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));

        // Act & Assert
        assertThatThrownBy(() -> couponService.applyCoupon(totalAmount, couponId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.ORDER_AMOUNT_BELOW_COUPON_MINIMUM.getMessage());
        verify(couponRepository, times(1)).findById(couponId);
    }
}