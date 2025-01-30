package kr.hhplus.be.server.domain.coupon.service;

import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
class CouponServiceConcurrencyTest {

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponRepository couponRepository;

    private Long couponId;
    private static final int THREAD_COUNT = 10; // 동시 요청 개수

    @BeforeEach
    void setUp() {
        // 초기 쿠폰 생성 (5개 제한)
        Coupon coupon = new Coupon("테스트 쿠폰", 5000L, 20000L, 5, LocalDate.now().plusDays(7));
        couponRepository.save(coupon);
        this.couponId = coupon.getId();
        log.info("[SETUP] 테스트용 쿠폰 생성 완료 - 쿠폰 ID: {}, 초기 남은 수량: {}", couponId, coupon.getRemainingQuantity());
    }

    @AfterEach
    void cleanData() {
        couponRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("쿠폰 수량 차감 동시성 테스트")
    void testConcurrentCouponQuantityDesc() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        List<Future<String>> results = new ArrayList<>();
        long startTime = System.currentTimeMillis();

        for (int i = 1; i <= THREAD_COUNT; i++) {
            int threadId = i;
            Future<String> future = executorService.submit(() -> {
                try {
//                    비관적 락
                    couponService.decreaseRemainingWithLock(couponId);
//                    couponService.decreaseRemaining(couponId);
                    log.info("[THREAD-{}] 요청 성공 - 쿠폰 발급 완료", threadId);
                    return "SUCCESS";
                } catch (Exception e) {
                    log.info("[THREAD-{}] 요청 실패 - 예외 발생: {}", threadId, e.getMessage());
                    return "FAILED";
                }
            });
            results.add(future);
        }

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        long endTime = System.currentTimeMillis();

        // 성공 요청 개수 계산
        long successCount = results.stream().filter(f -> {
            try {
                return f.get().equals("SUCCESS");
            } catch (Exception e) {
                return false;
            }
        }).count();

        long failCount = THREAD_COUNT - successCount;

        log.info("[TEST END] 테스트 종료 - 성공 요청: {}, 실패 요청: {}, 실행 시간: {}ms",
                successCount, failCount, (endTime - startTime));

        // 최종 쿠폰 잔여 수량 검증
        Coupon updatedCoupon = couponRepository.findById(couponId).orElseThrow();
        log.info("[RESULT] 최종 남은 쿠폰 수량: {} (expected: 0)", updatedCoupon.getRemainingQuantity());

        // 테스트 검증
        assertThat(successCount).isEqualTo(5); // 5개까지만 발급 가능
        assertThat(failCount).isEqualTo(THREAD_COUNT - 5); // 나머지는 실패해야 함
        assertThat(updatedCoupon.getRemainingQuantity()).isEqualTo(0); // 최종 쿠폰 수량 확인
    }
}