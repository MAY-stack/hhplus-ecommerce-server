package kr.hhplus.be.server.domain.coupon.service;

import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.exception.CouponNotFoundException;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CouponServiceImplTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private CouponServiceImpl couponService;

    @Test
    void 쿠폰이_존재하지_않으면_CouponNotFoundException을_던진다() {
        // Given
        Long couponId = 1L;
        when(couponRepository.findById(couponId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(CouponNotFoundException.class, () -> couponService.getCouponById(couponId));
        verify(couponRepository).findById(couponId);
    }

    @Test
    void 쿠폰_아이디로_조회하면_해당_아이디를_가진_Coupon_객체를_반환한다() {
        // Given
        Long couponId = 1L;
        Coupon coupon = Coupon.builder()
                .id(couponId)
                .build();
        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));

        // When
        Coupon result = couponService.getCouponById(couponId);

        // Then
        assertEquals(coupon, result);
        verify(couponRepository).findById(couponId);
    }

    @Test
    void 남은_쿠폰_수량_감소() {
        // Given
        Coupon coupon = spy(Coupon
                .builder()
                .remainingQuantity(10)
                .expirationDate(LocalDate.of(2025, 1, 30))
                .build());
        when(couponRepository.save(any(Coupon.class))).thenReturn(coupon);

        // When
        Coupon updatedCoupon = couponService.decreaseRemaining(coupon.getId());

        // Then
        verify(coupon).decreaseRemaining();
        verify(couponRepository).save(coupon);
        assertEquals(9, updatedCoupon.getRemainingQuantity());
    }

    @Test
    void 모든_쿠폰_ID_조회() {
        // Given
        Coupon coupon1 = Coupon.builder()
                .id(1L)
                .build();
        Coupon coupon2 = Coupon.builder()
                .id(2L)
                .build();
        when(couponRepository.findAll()).thenReturn(Arrays.asList(coupon1, coupon2));

        // When
        List<Long> result = couponService.getAllCouponIds();

        // Then
        assertEquals(Arrays.asList(1L, 2L), result);
        verify(couponRepository).findAll();
    }

    @Test
    void 만료된_쿠폰_ID_조회() {
        // Given
        Coupon coupon1 = Coupon.builder()
                .id(1L)
                .expirationDate(LocalDate.now().minusDays(1))
                .build();
        Coupon coupon2 = Coupon.builder()
                .id(2L)
                .expirationDate(LocalDate.now().minusDays(2))
                .build();

        when(couponRepository.findByExpirationDateBefore(LocalDate.now())).thenReturn(Arrays.asList(coupon1, coupon2));

        // When
        List<Long> result = couponService.getAllExpiredCouponIds();

        // Then
        assertEquals(Arrays.asList(1L, 2L), result);
        verify(couponRepository).findByExpirationDateBefore(LocalDate.now());
    }
}
