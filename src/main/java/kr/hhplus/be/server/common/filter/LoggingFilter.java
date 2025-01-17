package kr.hhplus.be.server.common.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

public class LoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(request);
        ContentCachingResponseWrapper cachedResponse = new ContentCachingResponseWrapper(response);

        try {
            // 요청 정보 로깅
            logRequest(cachedRequest);

            // 다음 필터 또는 서블릿으로 요청 전달
            filterChain.doFilter(cachedRequest, cachedResponse);
        } finally {
            // 응답 정보 로깅
            logResponse(cachedResponse);

            // 실제 응답 출력
            cachedResponse.copyBodyToResponse();
        }
    }

    private void logRequest(HttpServletRequest request) throws IOException {
        StringBuilder requestLog = new StringBuilder();
        requestLog.append("HTTP REQUEST\n");
        requestLog.append(String.format("Method: %s\n", request.getMethod()));
        requestLog.append(String.format("URI: %s\n", request.getRequestURI()));
        requestLog.append("Headers:\n");

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            requestLog.append(String.format("  %s: %s\n", headerName, request.getHeader(headerName)));
        }

        String requestBody = new String(request.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        requestLog.append(String.format("Body: %s\n", requestBody));

        logger.info(requestLog.toString());
    }

    private void logResponse(ContentCachingResponseWrapper response) throws IOException {
        StringBuilder responseLog = new StringBuilder();
        responseLog.append("HTTP RESPONSE\n");
        responseLog.append(String.format("Status: %d\n", response.getStatus()));
        responseLog.append("Headers:\n");

        response.getHeaderNames().forEach(headerName -> {
            responseLog.append(String.format("  %s: %s\n", headerName, response.getHeader(headerName)));
        });

        String responseBody = new String(response.getContentAsByteArray(), StandardCharsets.UTF_8);
        responseLog.append(String.format("Body: %s\n", responseBody));

        logger.info(responseLog.toString());
    }
}