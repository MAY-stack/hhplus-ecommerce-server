package kr.hhplus.be.server.domain.order.entity;

public enum OrderStatus {
    CREATED,
    PENDING_PAYMENT,  // 결제 대기
    COMPLETED, // 완료
    CANCELLED; // 취소됨
}