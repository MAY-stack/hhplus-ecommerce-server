package kr.hhplus.be.server.common.config;

import kr.hhplus.be.server.common.interceptor.UserValidationInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final UserValidationInterceptor userValidationInterceptor;

    public WebConfig(UserValidationInterceptor userValidationInterceptor) {
        this.userValidationInterceptor = userValidationInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userValidationInterceptor)
                .addPathPatterns(
                        "/api/v1/users/*/points",
                        "/api/v1/users/*/points/history",
                        "/api/v1/users/*/coupons",
                        "/api/v1/users/*/coupons/issuance"
                );
    }
}
