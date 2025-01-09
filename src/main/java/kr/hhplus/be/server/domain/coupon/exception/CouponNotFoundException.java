package kr.hhplus.be.server.domain.coupon.exception;

public class CouponNotFoundException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "쿠폰을 찾을 수 없습니다.";

    public CouponNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public CouponNotFoundException(String message) {
        super(message);
    }
}