package kr.hhplus.be.server.domain.point.service;

import kr.hhplus.be.server.domain.point.entity.Point;
import kr.hhplus.be.server.domain.point.repository.PointRepository;
import kr.hhplus.be.server.domain.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
class PointServiceConcurrencyTest {

    @Autowired
    private PointService pointService;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private UserService userService;

    private static final String USER_ID = "test_user";
    private static final int THREAD_COUNT = 10; // 10개의 동시 요청

    private static final Long DEDUCT_AMOUNT = 3000L;

    @BeforeEach
    void setUp() {
        // 1명의 포인트 생성
        Point point = pointService.createPoint(USER_ID);
        // 사용자에게 10,000 포인트 부여
        pointService.rechargePoint(USER_ID, 15000L);
    }

    @AfterEach
    void cleanData() {
        pointRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("포인트 차감 동시성 테스트")
    void testConcurrentPointDeduction() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        List<Future<String>> results = new ArrayList<>();
        long startTime = System.currentTimeMillis();

        for (int i = 1; i <= THREAD_COUNT; i++) {
            int threadId = i;
            Future<String> future = executorService.submit(() -> {
                try {
//                    비관적 락
                    pointService.deductPointWithLock(USER_ID, DEDUCT_AMOUNT);
//                    pointService.deductPoint(USER_ID, DEDUCT_AMOUNT);
                    log.info("[THREAD-{}] 요청 성공 - 포인트 차감 완료", threadId);
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

        // 최종 포인트 검증
        Point updatedPoint = pointRepository.findByUserId(USER_ID).orElseThrow();
        log.info("[RESULT] 최종 포인트 잔액: {} (expected: 0)", updatedPoint.getBalance());

        // 테스트 검증
        assertThat(successCount).isEqualTo(5); // 15,000 포인트 기준으로 최대 3회 차감 가능
        assertThat(failCount).isEqualTo(THREAD_COUNT - 5); // 나머지는 실패해야 함
        assertThat(updatedPoint.getBalance()).isEqualTo(0L); // 최종 잔액 확인
    }
}