package kr.hhplus.be.server.domain.coupon.entity;

import kr.hhplus.be.server.common.exception.ErrorMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CouponTest {

    @Test
    @DisplayName("유효한 값으로 쿠폰 생성을 요청하면 생성된 Coupon객체를 반환한다")
    void shouldCreateCouponSuccessfully_WhenValidInputsAreProvided() {
        // Arrange
        String title = "1000 Discount";
        Long discountAmount = 1000L;
        Long minimumOrderAmount = 5000L;
        Integer totalQuantity = 100;
        LocalDate expirationDate = LocalDate.now().plusDays(10);

        // Act
        Coupon coupon = new Coupon(title, discountAmount, minimumOrderAmount, totalQuantity, expirationDate);

        // Assert
        assertThat(coupon.getTitle()).isEqualTo(title);
        assertThat(coupon.getDiscountAmount()).isEqualTo(discountAmount);
        assertThat(coupon.getMinimumOrderAmount()).isEqualTo(minimumOrderAmount);
        assertThat(coupon.getTotalQuantity()).isEqualTo(totalQuantity);
        assertThat(coupon.getRemainingQuantity()).isEqualTo(totalQuantity);
        assertThat(coupon.getExpirationDate()).isEqualTo(expirationDate);
    }

    @Test
    @DisplayName("유효하지 않은 쿠폰명으로 쿠폰 생성을 요청하면 IllegalArgumentException을 던진다")
    void shouldThrowException_WhenTitleIsInvalid() {
        // Arrange
        String title = " ";
        Long discountAmount = 1000L;
        Long minimumOrderAmount = 5000L;
        Integer totalQuantity = 100;
        LocalDate expirationDate = LocalDate.now().plusDays(10);

        // Act & Assert
        assertThatThrownBy(() -> new Coupon(title, discountAmount, minimumOrderAmount, totalQuantity, expirationDate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.COUPON_TITLE_REQUIRED.getMessage());
    }

    @Test
    @DisplayName("0 이하의 할인 금액으로 쿠폰 생성을 요청하면 IllegalArgumentException을 던진다")
    void shouldThrowException_WhenDiscountAmountIsZeroOrNegative() {
        // Arrange
        String title = "10% Discount";
        Long discountAmount = 0L;
        Long minimumOrderAmount = 5000L;
        Integer totalQuantity = 100;
        LocalDate expirationDate = LocalDate.now().plusDays(10);

        // Act & Assert
        assertThatThrownBy(() -> new Coupon(title, discountAmount, minimumOrderAmount, totalQuantity, expirationDate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.MINIMUM_DISCOUNT_AMOUNT_VIOLATION.getMessage());
    }

    @Test
    @DisplayName("최소 주문금액을 0 이하로 쿠폰 생성을 요청하면 IllegalArgumentException을 던진다")
    void shouldThrowException_WhenMinimumOrderAmountIsZeroOrNegative() {
        // Arrange
        String title = "1000 Discount";
        Long discountAmount = 1000L;
        Long minimumOrderAmount = 0L;
        Integer totalQuantity = 100;
        LocalDate expirationDate = LocalDate.now().plusDays(10);

        // Act & Assert
        assertThatThrownBy(() -> new Coupon(title, discountAmount, minimumOrderAmount, totalQuantity, expirationDate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.MINIMUM_ORDER_AMOUNT_VIOLATION.getMessage());
    }

    @Test
    @DisplayName("쿠폰 수량을 0 이하로 쿠폰 생성을 요청하면 IllegalArgumentException을 던진다")
    void shouldThrowException_WhenTotalQuantityIsZeroOrNegative() {
        // Arrange
        String title = "1000 Discount";
        Long discountAmount = 1000L;
        Long minimumOrderAmount = 5000L;
        Integer totalQuantity = 0;
        LocalDate expirationDate = LocalDate.now().plusDays(10);

        // Act & Assert
        assertThatThrownBy(() -> new Coupon(title, discountAmount, minimumOrderAmount, totalQuantity, expirationDate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.MINIMUM_COUPON_QUANTITY_VIOLATION.getMessage());
    }

    @Test
    @DisplayName("생성 날짜 이전날짜를 만료일자로 쿠폰 생성을 요청하면 IllegalArgumentException을 던진다")
    void shouldThrowException_WhenExpirationDateIsInThePast() {
        // Arrange
        String title = "1000 Discount";
        Long discountAmount = 1000L;
        Long minimumOrderAmount = 5000L;
        Integer totalQuantity = 100;
        LocalDate expirationDate = LocalDate.now().minusDays(1);

        // Act & Assert
        assertThatThrownBy(() -> new Coupon(title, discountAmount, minimumOrderAmount, totalQuantity, expirationDate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.INVALID_EXPIRATION_DATE.getMessage());
    }

    @Test
    @DisplayName("유효한 쿠폰의 잔여수량이 남아 있으면 수량 차감을 성공한다.")
    void decreaseRemaining_ShouldDecreaseRemainingQuantity_WhenCouponIsValid() {
        // Arrange
        String title = "1000 Discount";
        Long discountAmount = 1000L;
        Long minimumOrderAmount = 5000L;
        Integer totalQuantity = 100;
        LocalDate expirationDate = LocalDate.now().plusDays(10);
        Coupon coupon = new Coupon(title, discountAmount, minimumOrderAmount, totalQuantity, expirationDate);

        // Act
        coupon.decreaseRemaining();

        // Assert
        assertThat(coupon.getRemainingQuantity()).isEqualTo(99); // 100 - 1
    }

    @Test
    @DisplayName("만료된 쿠폰의 수량을 감량요청하면 IllegalArgumentException을 던진다")
    void decreaseRemaining_ShouldThrowException_WhenCouponIsExpired() {
        // Arrange
        String title = "1000 Discount";
        Long discountAmount = 1000L;
        Long minimumOrderAmount = 5000L;
        Integer totalQuantity = 100;
        LocalDate expirationDate = LocalDate.now().minusDays(1);
        Coupon coupon = Coupon.builder()
                .title(title)
                .discountAmount(discountAmount)
                .minimumOrderAmount(minimumOrderAmount)
                .totalQuantity(totalQuantity)
                .expirationDate(expirationDate)
                .build();
        // Act & Assert
        assertThatThrownBy(coupon::decreaseRemaining)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.COUPON_EXPIRED.getMessage());
    }

    @Test
    @DisplayName("잔여 수량이 0인 쿠폰의 수량 차감요청은 IllegalArgumentException을 던진다")
    void decreaseRemaining_ShouldThrowException_WhenRemainingQuantityIsZero() {
        // Arrange
        String title = "1000 Discount";
        Long discountAmount = 1000L;
        Long minimumOrderAmount = 5000L;
        Integer totalQuantity = 1;
        LocalDate expirationDate = LocalDate.now().plusDays(10);
        Coupon coupon = new Coupon(title, discountAmount, minimumOrderAmount, totalQuantity, expirationDate);

        // Act
        coupon.decreaseRemaining();

        // Assert
        assertThatThrownBy(coupon::decreaseRemaining)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.COUPON_SOLD_OUT.getMessage());
    }
}
