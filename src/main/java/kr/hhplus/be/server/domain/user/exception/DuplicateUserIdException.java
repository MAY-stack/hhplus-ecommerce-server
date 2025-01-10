package kr.hhplus.be.server.domain.user.exception;

public class DuplicateUserIdException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "이미 존재하는 사용자 ID입니다.";

    public DuplicateUserIdException() {
        super(DEFAULT_MESSAGE);
    }

    public DuplicateUserIdException(String message) {
        super(message);
    }
}
