package kr.hhplus.be.server.application.coupon.facade;

import kr.hhplus.be.server.application.coupon.dto.CouponIssuanceInfoDto;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.exception.CouponAlreadyIssuedException;
import kr.hhplus.be.server.domain.coupon.exception.CouponSoldOutException;
import kr.hhplus.be.server.domain.coupon.repository.CouponIssuanceRepository;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.server.domain.coupon.service.CouponService;
import kr.hhplus.be.server.domain.user.entity.Users;
import kr.hhplus.be.server.domain.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(properties = {"spring.jpa.properties.javax.persistence.validation.mode=none"})
class CouponFacadeTest {

    @Autowired
    private CouponFacade couponFacade;

    @Autowired
    private UserService userService;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CouponIssuanceRepository couponIssuanceRepository;

    @Autowired
    private CouponService couponService;

    Users user;
    Coupon coupon;

    @BeforeEach
    void setUp() {
        // 사용자 생성
        user = userService.createUser("testUser", "Test User");

        // 쿠폰 생성
        coupon = couponService.createCoupon(
                "Test Coupon",
                500L,
                1000L,
                10,
                LocalDate.now().plusDays(7)
        );
    }

    @Test
    void 쿠폰_발급_테스트() {
        // Given
        Long couponId = coupon.getId();
        String userId = user.getId();

        // When
        CouponIssuanceInfoDto issuedCoupon = couponFacade.issueCoupon(couponId, userId);

        // Then
        assertThat(issuedCoupon).isNotNull();
        assertThat(issuedCoupon.getCouponId()).isEqualTo(couponId);
        assertThat(issuedCoupon.getUserId()).isEqualTo(userId);

        // 쿠폰 남은 수량 검증
        Coupon coupon = couponRepository.findById(couponId).orElseThrow();
        assertThat(coupon.getRemainingQuantity()).isEqualTo(9);
    }

    @Test
    void 발급된_쿠폰_목록_조회_테스트() {
        // Given
        Coupon coupon2 = couponService.createCoupon(
                "Test Coupon2",
                500L,
                2000L,
                10,
                LocalDate.now().plusDays(7)
        );


        Long couponId = coupon.getId();
        Long couponId2 = coupon2.getId();
        String userId = user.getId();


        // 쿠폰 발급
        couponFacade.issueCoupon(couponId, userId);
        couponFacade.issueCoupon(couponId2, userId);

        // Act
        List<CouponIssuanceInfoDto> issuedCoupons = couponFacade.getIssuedCouponList(userId);

        // Assert
        assertThat(issuedCoupons).isNotEmpty();
        assertThat(issuedCoupons.get(0).getCouponId()).isEqualTo(couponId);
        assertThat(issuedCoupons.get(1).getCouponId()).isEqualTo(couponId2);
        assertThat(issuedCoupons.get(0).getUserId()).isEqualTo(userId);

    }

    @Test
    void 없는_쿠폰_발급_테스트() {
        // Given
        Long invalidCouponId = 999L; // 존재하지 않는 쿠폰 ID
        String userId = user.getId();

        // When & Then
        assertThrows(
                RuntimeException.class,
                () -> couponFacade.issueCoupon(invalidCouponId, userId)
        );
    }

    @Test
    void 선착순_수량을_초과하는_쿠폰_발급요청은_CouponSoldOutException를_던진다() {
        // Arrange
        Long couponId = 1L; // 사전 저장된 쿠폰 ID
        String userIdPrefix = "testUser"; // 사용자 ID의 접두사
        int totalQuantity = 10; // 사전 저장된 쿠폰의 총 수량

        // 사용자 생성 및 쿠폰 발급
        for (int i = 1; i <= totalQuantity; i++) {
            String userId = userIdPrefix + i;
            userService.createUser(userId, "User ");
            couponFacade.issueCoupon(couponId, userId); // 쿠폰 발급
        }

        // Act & Assert: 남은 수량이 없으므로 발급 실패 테스트
        String extraUserId = userIdPrefix + (totalQuantity + 1);
        userService.createUser(extraUserId, "User " + (totalQuantity + 1));

        CouponSoldOutException exception = org.junit.jupiter.api.Assertions.assertThrows(
                CouponSoldOutException.class,
                () -> couponFacade.issueCoupon(couponId, extraUserId),
                "수량을 초과한 쿠폰 발급 요청은 예외를 던져야 합니다."
        );

        assertThat(exception.getMessage()).contains("쿠폰이 모두 소진되었습니다.");
    }

    @Test
    void 한_사용자가_같은쿠폰을_여러번_발급요청하면_CouponAlreadyIssuedException을_던진다() {
        // Given
        String userId = user.getId();
        Long couponId = coupon.getId();

        couponFacade.issueCoupon(couponId, userId);

        // When & Then
        CouponAlreadyIssuedException exception = assertThrows(CouponAlreadyIssuedException.class,
                () -> couponFacade.issueCoupon(couponId, userId));

        assertThat(exception.getMessage()).contains("이미 발급받은 쿠폰입니다.");
    }

    @Test
    void 여러_사용자가_동시에_발급_요청하면_쿠폰의_수량만큼만_발급처리_된다() throws InterruptedException, ExecutionException {
        // Given
        Long couponId = coupon.getId(); // 테스트 쿠폰 ID
        int totalQuantity = coupon.getTotalQuantity(); // 쿠폰의 총 수량
        int numberOfUsers = 20; // 발급 요청을 보낼 사용자 수
        ExecutorService executorService = Executors.newFixedThreadPool(20);

        // 사용자 데이터 준비
        List<String> userIds = new ArrayList<>();
        for (int i = 0; i < numberOfUsers; i++) {
            String userId = "testUser" + i;
            Users user = userService.createUser(userId, "User " + i);
            userIds.add(user.getId());
        }

        // When
        List<Future<String>> futures = new ArrayList<>();
        for (String userId : userIds) {
            futures.add(executorService.submit(() -> {
                try {
                    couponFacade.issueCoupon(couponId, userId);
                    return userId + ": 발급 성공";
                } catch (RuntimeException e) {
                    return userId + ": 발급 실패 - " + e.getMessage();
                }
            }));
        }

        // Executor 종료
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        // 결과 수집
        List<String> results = new ArrayList<>();

        for (Future<String> future : futures) {
            results.add(future.get());
        }

        // Then
        long successCount = results.stream().filter(result -> result.contains("발급 성공")).count();
        long failureCount = results.stream().filter(result -> result.contains("발급 실패")).count();

        assertThat(successCount).isEqualTo(totalQuantity); // 성공한 발급 수는 쿠폰의 총 수량과 같아야 함
        assertThat(failureCount).isEqualTo(numberOfUsers - totalQuantity); // 실패한 발급 수는 요청 수 - 성공 수와 같아야 함

        // 성공 및 실패 로그 출력
        results.forEach(System.out::println);
    }
}
