package kr.hhplus.be.server.domain.point.exception;

public class InvalidPointAmountException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "충전 금액은 1~100만 포인트 범위 내에서만 가능합니다.";

    public InvalidPointAmountException() {
        super(DEFAULT_MESSAGE);
    }

    public InvalidPointAmountException(String message) {
        super(message);
    }
}