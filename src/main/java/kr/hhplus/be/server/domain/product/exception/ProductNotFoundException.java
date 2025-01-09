package kr.hhplus.be.server.domain.product.exception;

public class ProductNotFoundException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "제품 정보를 찾을 수 없습니다.";

    public ProductNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public ProductNotFoundException(String message) {
        super(message);
    }
}