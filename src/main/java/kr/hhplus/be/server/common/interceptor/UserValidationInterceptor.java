package kr.hhplus.be.server.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserValidationInterceptor implements HandlerInterceptor {

    private final UserService userService;

    // userId가 포함될 수 있는 API 패턴
    private static final Pattern USER_ID_PATTERN = Pattern.compile(
            "/api/v1/users/([^/]+)/.*" // /api/v1/users/{userId}/xxx
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();

        // userId 검증이 필요한 경우인지 확인
        if (requiresUserValidation(uri)) {
            Matcher matcher = USER_ID_PATTERN.matcher(uri);
            if (matcher.find()) {
                String userId = matcher.group(1); // 첫 번째 그룹에서 userId 추출
                // 사용자 검증
                User user = userService.getUserById(userId);
            }
        }
        return true; // 다음 단계 진행
    }

    // userId 검증이 필요한 API 경로인지 확인
    private boolean requiresUserValidation(String uri) {
        return uri.matches("^/api/v1/users/[^/]+/points(/history)?$") ||  // 포인트 조회, 내역 조회, 충전
                uri.matches("^/api/v1/users/[^/]+/coupons(/issuance)?$");  // 사용자가 발급받은 쿠폰 조회
    }
}