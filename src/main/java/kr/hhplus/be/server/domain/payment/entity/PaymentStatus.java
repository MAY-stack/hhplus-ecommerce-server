package kr.hhplus.be.server.domain.payment.entity;

public enum PaymentStatus {
    PENDING,
    COMPLETED, // 결제 완료
    FAILED;    // 결제 실패
}