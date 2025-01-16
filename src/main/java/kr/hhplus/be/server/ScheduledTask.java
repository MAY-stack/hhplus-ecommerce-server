package kr.hhplus.be.server;

import kr.hhplus.be.server.application.coupon.facade.CouponFacade;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledTask {
    private final CouponFacade couponFacade;
    private final Logger logger = LoggerFactory.getLogger(ScheduledTask.class);

    /**
     * 사용하지 않은 쿠폰들을 만료시키는 스케줄러 태스크
     * 매일 자정에 실행
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void expireUnusedCoupons() {
        try {
            int expireCouponCount = couponFacade.expireUnusedCoupons();
            // 로그로 실행 결과 기록
            logger.info("쿠폰 만료 작업 완료: " + expireCouponCount + "건 처리됨");
        } catch (Exception e) {
            // 예외 발생 시 로그 처리
            logger.info("쿠폰 만료 작업 중 오류 발생: " + e.getMessage());
        }

    }
}
