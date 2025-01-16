package kr.hhplus.be.server.common.exception;

import lombok.Getter;

@Getter
public enum ErrorMessage {

    // 사용자
    USER_NOT_FOUND("사용자 정보를 찾을 수 없습니다."),
    USER_ID_ALREADY_EXISTS("이미 존재하는 사용자 ID입니다."),
    USER_ID_REQUIRED("사용자 아이디는 필수입니다."),
    USER_NAME_REQUIRED("사용자 이름은 필수입니다."),

    // 쿠폰
    COUPON_ID_REQUIRED("쿠폰 아이디는 필수입니다."),
    COUPON_TITLE_REQUIRED("쿠폰 제목은 필수입니다."),
    MINIMUM_DISCOUNT_AMOUNT_VIOLATION("할인 금액은 0보다 커야 합니다."),
    MINIMUM_ORDER_AMOUNT_VIOLATION("최소 주문 금액은 0보다 커야 합니다."),
    MINIMUM_COUPON_QUANTITY_VIOLATION("쿠폰 총 수량은 0보다 커야 합니다."),
    EXPIRATION_DATE_REQUIRE("만료일자가 필요합니다."),
    INVALID_EXPIRATION_DATE("쿠폰 만료일은 오늘 이후여야 합니다."),
    COUPON_NOT_FOUND("쿠폰 정보를 찾을 수 없습니다."),
    COUPON_EXPIRED("만료된 쿠폰입니다."),
    COUPON_ALREADY_USED("이미 사용된 쿠폰입니다."),
    COUPON_SOLD_OUT("쿠폰이 모두 소진 되었습니다."),
    ORDER_AMOUNT_BELOW_COUPON_MINIMUM("주문 금액이 쿠폰 사용 최소금액 이하입니다."),
    COUPON_NOT_OWNED_BY_USER("사용자가 발급받은 쿠폰이 아닙니다."),
    COUPON_ALREADY_ISSUED("이미 발급받은 쿠폰입니다."),

    //제품
    PRODUCT_NOT_FOUND("제품 정보를 찾을 수 없습니다."),
    PRODUCT_SOLD_OUT("제품이 품절 되었습니다."),
    PRODUCT_NAME_REQUIRED("제품명은 필수입니다."),
    MINIMUM_STOCK_VIOLATION("재고는 최소 1개 이상이어야 합니다."),
    MINIMUM_PRICE_VIOLATION("제품 가격은 최소 1원 이상이어야 합니다."),
    CATEGORY_ID_REQUIRED("카테고리 아이디는 필수 입니다."),
    CATEGORY_NAME_REQUIRED("카테고리 이름은 필수 입니다"),

    // 포인트
    POINT_NOT_FOUND("포인트 정보를 찾을 수 없습니다."),
    INITIAL_BALANCE_NEGATIVE("초기잔고는 0미만일 수 없습니다."),
    MINIMUM_RECHARGE_POINTS_VIOLATION("최소 충전 포인트는 1 이상 입니다."),
    MAXIMUM_RECHARGE_POINTS_VIOLATION("최대 충전 포인트는 1_000_000 미만 입니다."),
    BALANCE_EXCEEDS_LIMIT("최대 보유 포인트는 10_000_000을 초과 할 수 없습니다."),
    MINIMUM_USAGE_POINTS_VIOLATION("최소 사용 포인트는 1 이상 입니다."),
    MAXIMUM_USAGE_POINTS_VIOLATION("최대 사용 포인트는 10_000_000 미만 입니다."),
    INSUFFICIENT_POINT_BALANCE("보유 포인트가 부족합니다."),

    // 포인트 히스토리
    POINT_ID_REQUIRED("포인트 아이디는 필수입니다."),
    POINT_HISTORY_TYPE_REQUIRED("포인트 내역은 필수입니다."),
    POINT_REQUIRED("포인트는 필수입니다"),


    // 주문
    ORDER_ID_REQUIRED("주문 아이디는 필수 입니다."),
    PRODUCT_ID_REQUIRED("제품 아이디는 필수 입니다."),
    MINIMUM_PRODUCT_PRICE_VIOLATION("최소 제품 단가는 1원입니다."),
    MINIMUM_ORDER_QUANTITY_VIOLATION("최소 주문 수량은 1개입니다."),

    // 결제
    MINIMUM_PAYMENT_AMOUNT_VIOLATION("최소 결제 금액은 1원입니다."),
    PAYMENT_STATUS_REQUIRED("결제 상태는 필수입니다.");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

}
