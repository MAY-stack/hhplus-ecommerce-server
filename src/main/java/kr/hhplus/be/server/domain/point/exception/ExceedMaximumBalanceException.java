package kr.hhplus.be.server.domain.point.exception;

public class ExceedMaximumBalanceException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "최대 보유 잔고는 1000만 포인트를 초과할 수 없습니다.";

    public ExceedMaximumBalanceException() {
        super(DEFAULT_MESSAGE);
    }

    public ExceedMaximumBalanceException(String message) {
        super(message);
    }
}