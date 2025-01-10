package kr.hhplus.be.server.domain.product.exception;

public class InsufficientStockException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "재고가 부족합니다.";

    public InsufficientStockException() {
        super(DEFAULT_MESSAGE);
    }

    public InsufficientStockException(String message) {
        super(message);
    }
}