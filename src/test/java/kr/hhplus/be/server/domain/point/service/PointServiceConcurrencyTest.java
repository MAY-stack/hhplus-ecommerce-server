package kr.hhplus.be.server.domain.point.service;

import kr.hhplus.be.server.domain.point.entity.Point;
import kr.hhplus.be.server.domain.point.repository.PointRepository;
import kr.hhplus.be.server.domain.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class PointServiceConcurrencyTest {

    @Autowired
    private PointService pointService;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private UserService userService;

    private String userId;
    private static final int THREAD_COUNT = 10; // 10개의 동시 요청

    @BeforeEach
    void setUp() {
        // 1명의 사용자 생성
        userId = "user1";
        userService.createUser(userId, "테스트유저");

        // 사용자에게 10,000 포인트 부여
        Point point = pointService.createPoint(userId);
        pointService.rechargePoint(userId, 10000L);
    }

    @Test
    void 포인트_차감_동시성_테스트() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        List<Future<String>> results = new ArrayList<>();

        for (int i = 1; i <= THREAD_COUNT; i++) {
            Future<String> future = executorService.submit(() -> {
                try {
                    pointService.deductPoint(userId, 3000L);
                    return "SUCCESS";
                } catch (Exception e) {
                    return "FAILED";
                }
            });
            results.add(future);
        }

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        // ✅ 테스트 검증
        long successCount = results.stream().filter(f -> {
            try {
                return f.get().equals("SUCCESS");
            } catch (Exception e) {
                return false;
            }
        }).count();

        long failCount = THREAD_COUNT - successCount;

        // ❗ 성공한 요청은 3건이어야 함 (10,000 포인트 기준으로 최대 3회 차감 가능)
        assertThat(successCount).isEqualTo(3);

        // ❗ 실패한 요청은 7건이어야 함
        assertThat(failCount).isEqualTo(THREAD_COUNT - 3);

        // ✅ 최종 포인트 잔액이 1,000인지 확인
        Point updatedPoint = pointRepository.findByUserId(userId).orElseThrow();
        assertThat(updatedPoint.getBalance()).isEqualTo(1000L);
    }
}