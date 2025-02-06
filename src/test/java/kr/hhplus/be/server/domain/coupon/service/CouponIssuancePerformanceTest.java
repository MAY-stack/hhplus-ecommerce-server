package kr.hhplus.be.server.domain.coupon.service;

import kr.hhplus.be.server.domain.coupon.repository.CouponIssuanceRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@Testcontainers
public class CouponIssuancePerformanceTest {

    @Autowired
    private CouponIssuanceRepository couponIssuanceRepository;

    @Autowired
    private CouponIssuanceService couponIssuanceService;

    @Autowired
    private RedisCouponIssuanceService redisCouponIssuanceService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String COUPON_ISSUED_KEY_PREFIX = "coupon:issued:";
    private static final Long TEST_COUPON_ID = 1001L;
    private static final int TEST_ITERATIONS = 500; // 테스트 반복 횟수

    @AfterEach
    void deleteData() {
        // 기존 DB 데이터 삭제 및 Redis 초기화
        couponIssuanceRepository.deleteAllInBatch();
        redisTemplate.delete(COUPON_ISSUED_KEY_PREFIX + TEST_COUPON_ID);
    }

    @Test
    void compareCouponIssuancePerformance() {
        long dbTotalTime = 0;
        long redisTotalTime = 0;

        for (int i = 0; i < TEST_ITERATIONS; i++) {
            String userId = "user" + i;

            // **1. JPA 방식 실행 시간 측정**
            long dbStartTime = System.nanoTime();
            couponIssuanceService.issueCoupon(TEST_COUPON_ID, userId);
            long dbEndTime = System.nanoTime();
            dbTotalTime += (dbEndTime - dbStartTime);

            couponIssuanceRepository.deleteAllInBatch();

            // **2️. Redis 방식 실행 시간 측정**
            long redisStartTime = System.nanoTime();
            redisCouponIssuanceService.issueCoupon(TEST_COUPON_ID, userId);
            long redisEndTime = System.nanoTime();
            redisTotalTime += (redisEndTime - redisStartTime);
        }

        double dbAvgTime = (double) dbTotalTime / TEST_ITERATIONS / 1_000_000;
        double redisAvgTime = (double) redisTotalTime / TEST_ITERATIONS / 1_000_000;

        log.info(String.format("JPA 평균 실행 시간: %.4f ms", dbAvgTime));
        log.info(String.format("Redis 평균 실행 시간: %.4f ms", redisAvgTime));

        assertThat(redisAvgTime).isLessThan(dbAvgTime); // Redis가 더 빠른지 검증
    }
}
