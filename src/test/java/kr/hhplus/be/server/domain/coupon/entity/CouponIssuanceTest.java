package kr.hhplus.be.server.domain.coupon.entity;

import kr.hhplus.be.server.common.exception.ErrorMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CouponIssuanceTest {

    @Test
    @DisplayName("쿠폰 아이디와 사용자 아이디로 쿠폰발급객체를 생성하면 생성된 CouponIssuance 객체를 반환한다")
    void constructor_ShouldInitializeFields() {
        // Arrange
        Long couponId = 1L;
        String userId = "user123";

        // Act
        CouponIssuance couponIssuance = new CouponIssuance(couponId, userId);

        // Assert
        assertThat(couponIssuance.getId()).isNotNull();
        assertThat(couponIssuance.getCouponId()).isEqualTo(couponId);
        assertThat(couponIssuance.getUserId()).isEqualTo(userId);
        assertThat(couponIssuance.getStatus()).isEqualTo(IssuedCouponStatus.ISSUED);
        assertThat(couponIssuance.getIssuedAt()).isNull(); // @CreationTimestamp는 JPA에서 관리
    }

    @Test
    @DisplayName("사용한 상태로 변경을 요청하면 상태와 사용일자가 반영된다")
    void changeStatusToUsed_ShouldUpdateStatusAndUsedAt() {
        // Arrange
        Long couponId = 1L;
        String userId = "user123";
        CouponIssuance couponIssuance = new CouponIssuance(couponId, userId);

        // Act
        couponIssuance.changeStatusToUsed(userId);

        // Assert
        assertThat(couponIssuance.getStatus()).isEqualTo(IssuedCouponStatus.USED);
        assertThat(couponIssuance.getUsedAt()).isNotNull();
    }

    @Test
    @DisplayName("사용자 아이디가 null인데 쿠폰 사용을 요청하면 IllegalArgumentException을 던진다")
    void changeStatusToUsed_ShouldThrowException_WhenUserIdIsNull() {
        // Arrange
        Long couponId = 1L;
        String userId = "user123";
        CouponIssuance couponIssuance = new CouponIssuance(couponId, userId);

        // Act & Assert
        assertThatThrownBy(() -> couponIssuance.changeStatusToUsed(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.USER_ID_REQUIRED.getMessage());
    }

    @Test
    @DisplayName("만료된 쿠폰의 사용을 요청하면 IllegalArgumentException을 던진다")
    void changeStatusToUsed_ShouldThrowException_WhenStatusIsExpired() {
        // Arrange
        Long couponId = 1L;
        String userId = "user123";
        CouponIssuance couponIssuance = new CouponIssuance(couponId, userId);
        couponIssuance.changeStatusExpire();

        // Act & Assert
        assertThatThrownBy(() -> couponIssuance.changeStatusToUsed(userId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.COUPON_EXPIRED.getMessage());
    }

    @Test
    @DisplayName("이미 사용된 쿠폰에 사용처리 요청을 하면 IllegalArgumentException을 던진다")
    void changeStatusToUsed_ShouldThrowException_WhenCouponIsAlreadyUsed() {
        // Arrange
        Long couponId = 1L;
        String userId = "user123";
        CouponIssuance couponIssuance = new CouponIssuance(couponId, userId);
        couponIssuance.changeStatusToUsed(userId);

        // Act & Assert
        assertThatThrownBy(() -> couponIssuance.changeStatusToUsed(userId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.COUPON_ALREADY_USED.getMessage());
    }

    @Test
    @DisplayName("발급한 사용자 아이디와 일치하지 않는 사용자 아이디로 쿠폰 사용을 요청하면 IllegalArgumentException을 던진다")
    void changeStatusToUsed_ShouldThrowException_WhenUserIdDoesNotMatch() {
        // Arrange
        Long couponId = 1L;
        String userId = "user123";
        String otherUserId = "otherUser";
        CouponIssuance couponIssuance = new CouponIssuance(couponId, userId);

        // Act & Assert
        assertThatThrownBy(() -> couponIssuance.changeStatusToUsed(otherUserId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.COUPON_NOT_OWNED_BY_USER.getMessage());
    }

    @Test
    @DisplayName("만료되거나 사용하지 않은 쿠폰의 상태를 만료상태로 변경할 수 있다")
    void changeStatusExpire_ShouldUpdateStatusToExpired() {
        // Arrange
        Long couponId = 1L;
        String userId = "user123";
        CouponIssuance couponIssuance = new CouponIssuance(couponId, userId);

        // Act
        couponIssuance.changeStatusExpire();

        // Assert
        assertThat(couponIssuance.getStatus()).isEqualTo(IssuedCouponStatus.EXPIRED);
    }

    @Test
    @DisplayName("쿠폰이 사용된 경우에는 쿠폰 만료 처리 요청에 IllegalArgumentException을 던진다")
    void changeStatusExpire_ShouldThrowException_WhenCouponIsAlreadyUsed() {
        // Arrange
        Long couponId = 1L;
        String userId = "user123";
        CouponIssuance couponIssuance = new CouponIssuance(couponId, userId);
        couponIssuance.changeStatusToUsed(userId);

        // Act & Assert
        assertThatThrownBy(couponIssuance::changeStatusExpire)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.COUPON_ALREADY_USED.getMessage());
    }

    @Test
    @DisplayName("이미 만료된 쿠폰을 만료처리 요청하면 IllegalArgumentException을 던진다")
    void changeStatusExpire_ShouldThrowException_WhenCouponIsAlreadyExpired() {
        // Arrange
        Long couponId = 1L;
        String userId = "user123";
        CouponIssuance couponIssuance = new CouponIssuance(couponId, userId);
        couponIssuance.changeStatusExpire();

        // Act & Assert
        assertThatThrownBy(couponIssuance::changeStatusExpire)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.COUPON_EXPIRED.getMessage());
    }
}
