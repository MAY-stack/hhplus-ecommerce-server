package kr.hhplus.be.server.domain.order.entity;

public enum OrderStatus {
    CREATED,
    PENDING_PAYMENT,  // 결제 대기
    PAYMENT_FAILED,    // 결제 실패
    COMPLETED, // 완료
    CANCELLED; // 취소됨
}