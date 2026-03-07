package com.kezul.backend.global.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

import com.kezul.backend.global.logging.AppLog;

/**
 * Filter 체인에서 발생하는 예외를 GlobalExceptionHandler 로 전달하는 Filter.
 *
 * Filter 단계(예: JwtAuthenticationFilter)에서 예외가 발생하면
 * Spring MVC의 DispatcherServlet 에 도달하지 못해 @RestControllerAdvice 가 동작하지 않습니다.
 * 이를 해결하기 위해 Filter 최전단에 이 필터를 두고, 발생한 예외를 HandlerExceptionResolver 로 위임합니다.
 */
@Slf4j
@Component
public class ExceptionDelegatorFilter extends OncePerRequestFilter {

    private final HandlerExceptionResolver exceptionResolver;

    public ExceptionDelegatorFilter(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver exceptionResolver) {
        this.exceptionResolver = exceptionResolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            AppLog.exceptionDelegated(log, e);
            exceptionResolver.resolveException(request, response, null, e);
        }
    }
}
