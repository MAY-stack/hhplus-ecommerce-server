package kr.hhplus.be.server.common.config;

import kr.hhplus.be.server.common.filter.LoggingFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<LoggingFilter> loggingFilter() {
        FilterRegistrationBean<LoggingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new LoggingFilter());
        registrationBean.addUrlPatterns("/api/v1/*"); // 특정 패턴에만 필터 적용
        registrationBean.setOrder(1); // 필터 실행 순서 지정
        return registrationBean;
    }
}

