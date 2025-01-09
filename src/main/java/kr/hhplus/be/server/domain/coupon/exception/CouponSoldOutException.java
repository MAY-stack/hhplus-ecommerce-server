package kr.hhplus.be.server.domain.coupon.exception;

public class CouponSoldOutException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "쿠폰이 모두 소진되었습니다.";

    public CouponSoldOutException() {
        super(DEFAULT_MESSAGE);
    }

    public CouponSoldOutException(String message) {
        super(message);
    }
}