package kr.hhplus.be.server.common.exception;

import io.swagger.v3.oas.annotations.Hidden;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Hidden
public class GlobalExceptionHandler {
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException e) {
        ErrorResponse errorResponse = new ErrorResponse("BAD_REQUEST_ERROR", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        ErrorResponse errorResponse = new ErrorResponse("ILLEGAL_ARGUMENT_ERROR", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        ErrorResponse errorResponse = new ErrorResponse("GENERAL_ERROR", "서버 오류가 발생했습니다.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

}

/*
    IllegalArgumentException: 허용하지 않는 값이 인수로 건네졌을 때 (null은 NPE로 처리)
    IllegalStateException: 객체가 메서드를 수행하기 적절하지 않은 상태일 때, 어떤 인수가 들어왔어도 실패했을 경우
    NullPointerExceptionL null을 허용하지 않는 메서드에 null을 건넸을 때
    IndexOutOfBoundsException: 인덱스가 범위를 넘어섰을 때
    ConcurrentModificationException: 허용하지 않는 동시 수정이 발견됐을 때
    UnSupportedOperationException: 호출한 메서드를 지원하지 않을 때
 */