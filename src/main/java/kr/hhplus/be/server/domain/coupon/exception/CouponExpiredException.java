package kr.hhplus.be.server.domain.coupon.exception;

public class CouponExpiredException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "만료된 쿠폰입니다.";

    public CouponExpiredException() {
        super(DEFAULT_MESSAGE);
    }

    public CouponExpiredException(String message) {
        super(message);
    }
}