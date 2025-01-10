package kr.hhplus.be.server.domain.coupon.service;

import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.entity.CouponIssuance;
import kr.hhplus.be.server.domain.coupon.exception.CouponAlreadyIssuedException;
import kr.hhplus.be.server.domain.coupon.repository.CouponIssuanceRepository;
import kr.hhplus.be.server.domain.user.entity.Users;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponIssuanceServiceImplTest {

    @Mock
    private CouponIssuanceRepository couponIssuanceRepository;

    @InjectMocks
    private CouponIssuanceServiceImpl couponIssuanceService;

    private Coupon coupon;

    @Test
    void 이미_발급된_쿠폰을_발급_요청하면_CouponAlreadyIssuedException을_던진다() {
        // given: 이미 발급된 경우 설정
        when(couponIssuanceRepository.existsByCouponIdAndUserId(coupon.getId(), user.getId()))
                .thenReturn(true);

        // when & then: 예외 발생 검증
        assertThrows(CouponAlreadyIssuedException.class, () -> {
            couponIssuanceService.issueCoupon(coupon, user);
        });

        verify(couponIssuanceRepository, never()).save(any(CouponIssuance.class));
    }

    private Users user;

    @BeforeEach
    void setUp() {
        // 테스트용 Coupon 및 User 생성
        coupon = Coupon.builder()
                .id(1L)
                .title("테스트용 쿠폰")
                .minimumOrderAmount(20000L)
                .discountAmount(5000L)
                .totalQuantity(30)
                .remainingQuantity(30)
                .expirationDate(LocalDate.of(2025, 1, 30))
                .build();

        user = new Users("user123", "사용자");
    }

    @Test
    void 발급한적없는_유효기간내의_잔여수량이남은_쿠폰을_발급요청_시_발급된_쿠폰을_반환한다() {
        // given: 중복 발급이 없다고 설정
        when(couponIssuanceRepository.existsByCouponIdAndUserId(coupon.getId(), user.getId()))
                .thenReturn(false);

        when(couponIssuanceRepository.save(any(CouponIssuance.class)))
                .thenAnswer(invocation -> invocation.getArgument(0)); // 전달된 객체 반환

        // when: 쿠폰 발급 시도
        CouponIssuance issuance = couponIssuanceService.issueCoupon(coupon, user);

        // then: 결과 검증
        assertNotNull(issuance);
        assertEquals(coupon.getId(), issuance.getCouponId());
        assertEquals(user.getId(), issuance.getUserId());
        verify(couponIssuanceRepository, times(1)).save(any(CouponIssuance.class));
    }

}
