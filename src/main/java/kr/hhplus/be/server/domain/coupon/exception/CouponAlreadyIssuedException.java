package kr.hhplus.be.server.domain.coupon.exception;

public class CouponAlreadyIssuedException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "이미 발급받은 쿠폰입니다.";

    public CouponAlreadyIssuedException() {
        super(DEFAULT_MESSAGE);
    }

    public CouponAlreadyIssuedException(String message) {
        super(message);
    }
}