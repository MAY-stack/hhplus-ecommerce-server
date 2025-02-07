package kr.hhplus.be.server.domain.coupon.service;

import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@SpringBootTest
class CouponServicePerformanceTest {

    @Autowired
    private RedisCouponService redisCouponService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponRepository couponRepository;

    private static final int THREAD_COUNT = 100; // 병렬 실행할 스레드 수

    private final String title = "Test Coupon";
    private final Long discountAmount = 1000L;
    private final Long minimumOrderAmount = 50000L;
    private final Integer totalQuantity = 200;
    private final LocalDate expirationDate = LocalDate.now().plusDays(30);
    private Coupon coupon;

    @AfterEach
    void cleanData() {
        couponRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("DB Lock 이용 잔여 쿠폰 수량 차감 시간 테스트")
    void testCouponServiceWithLockPerformance() throws InterruptedException, ExecutionException {
        coupon = couponService.createCoupon(title, discountAmount, minimumOrderAmount, totalQuantity, expirationDate);
        long startTime = System.nanoTime();

        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        List<Callable<Coupon>> tasks = new ArrayList<>();

        // 병렬로 비관적 락 기반 수량 감소 메서드 호출
        for (int i = 0; i < THREAD_COUNT; i++) {
            tasks.add(() -> couponService.decreaseRemainingWithLock(coupon.getId()));
        }

        List<Future<Coupon>> futures = executorService.invokeAll(tasks);
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);

        // 결과 검증
        for (Future<Coupon> future : futures) {
            Coupon result = future.get();
            assertNotNull(result);
        }

        long endTime = System.nanoTime();
        long totalTime = endTime - startTime;
        double avgTime = (double) (endTime - startTime) / 1_000_000 / THREAD_COUNT;
        log.info(String.format("DB 평균 실행 시간: %.4f ms", avgTime));
    }

    @Test
    @DisplayName("Redis 이용 잔여 쿠폰 수량 차감 시간 테스트")
    void testRedisCouponServicePerformance() throws InterruptedException, ExecutionException {
        coupon = redisCouponService.createCouponWithCache(title, discountAmount, minimumOrderAmount, totalQuantity, expirationDate);

        long startTime = System.nanoTime();

        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        List<Future<?>> futures = new ArrayList<>();

        // 병렬로 Redis 기반 수량 감소 메서드 호출
        for (int i = 0; i < THREAD_COUNT; i++) {
            futures.add(executorService.submit(() -> redisCouponService.decreaseRemainingWithCache(coupon.getId())));
        }

        // 모든 작업 완료 대기
        for (Future<?> future : futures) {
            future.get(); // 예외 발생 시 처리
        }

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);

        long endTime = System.nanoTime();
        double avgTime = (double) (endTime - startTime) / 1_000_000 / THREAD_COUNT;
        log.info(String.format("Redis 평균 실행 시간: %.4f ms", avgTime));
    }

}
