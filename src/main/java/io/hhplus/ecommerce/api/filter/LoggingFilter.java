package io.hhplus.ecommerce.api.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class LoggingFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        long startTime = System.currentTimeMillis();

        // 요청 로그
        log.info("Incoming Request: method={}, uri={}, remoteAddress={}",
                httpRequest.getMethod(),
                httpRequest.getRequestURI(),
                httpRequest.getRemoteAddr());

        // 다음 필터로 요청
        filterChain.doFilter(servletRequest, servletResponse);

        // 응답 로깅 및 처리 시간 계산
        long time = System.currentTimeMillis() - startTime;
        log.info("Out Response: status={}, EndTime={}ms",
                httpResponse.getStatus(),
                time);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
