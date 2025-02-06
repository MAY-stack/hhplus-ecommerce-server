package kr.hhplus.be.server.infrastructure.cache;

public enum RedisKeyPrefix {
    // 쿠폰별 발급 유저 ID
    COUPON_ISSUED_USER("coupon:issued:user"),

    // 쿠폰별 잔여 수량
    COUPON_REMAINING_QUANTITY("coupon:remaining:quantity");

    private final String prefix;

    RedisKeyPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String format(Object... args) {
        return String.format(prefix + ":%s", args);
    }

    public String getPrefix() {
        return prefix;
    }
}
