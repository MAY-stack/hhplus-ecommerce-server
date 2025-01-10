package kr.hhplus.be.server.application.payment;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.application.external.dto.ExternalRequestDto;
import kr.hhplus.be.server.application.external.service.ExternalDataPlatformService;
import kr.hhplus.be.server.application.point.PointFacade;
import kr.hhplus.be.server.domain.order.entity.Orders;
import kr.hhplus.be.server.domain.payment.entity.Payment;
import kr.hhplus.be.server.domain.payment.entity.PaymentStatus;
import kr.hhplus.be.server.domain.payment.service.PaymentService;
import kr.hhplus.be.server.domain.point.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentFacade {
    private final PointService pointService;
    private final PaymentService paymentService;
    private final PointFacade pointFacade;
    private final ExternalDataPlatformService externalDataPlatformService;

    @Transactional
    public Payment processPayment(Orders orders) {
        Payment payment;
        try {
            // 포인트 차감 처리
            pointFacade.deductPointWithHistory(orders.getUserId(), orders.getFinalAmount());
            // 결제 성공 처리
            payment = new Payment(orders.getId(), orders.getFinalAmount(), PaymentStatus.COMPLETED);
        } catch (Exception e) {
            // 결제 실패 처리
            payment = new Payment(orders.getId(), orders.getFinalAmount(), PaymentStatus.FAILED);
        }
        // Payment 저장
        payment = paymentService.save(payment);

        // 외부 플랫폼 데이터 전송
        externalDataPlatformService.sendData(new ExternalRequestDto(orders));

        return payment;
    }
}
