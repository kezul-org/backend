package com.kezul.backend.global.security.filter;

import static org.mockito.Mockito.*;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.HandlerExceptionResolver;

@ExtendWith(MockitoExtension.class)
class ExceptionDelegatorFilterTest {

    @Mock
    private HandlerExceptionResolver handlerExceptionResolver;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private ExceptionDelegatorFilter exceptionDelegatorFilter;

    @Test
    @DisplayName("정상적인 필터 체인은 예외를 위임하지 않고 통과시킨다")
    void testDoFilterInternal_Success() throws Exception {
        // given
        doNothing().when(filterChain).doFilter(request, response);

        // when
        exceptionDelegatorFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(handlerExceptionResolver, never()).resolveException(any(), any(), any(), any());
    }

    @Test
    @DisplayName("필터 체인에서 발생한 예외는 HandlerExceptionResolver로 위임된다")
    void testDoFilterInternal_WithException() throws Exception {
        // given
        RuntimeException exception = new RuntimeException("Test exception");
        doThrow(exception).when(filterChain).doFilter(request, response);

        // when
        exceptionDelegatorFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(handlerExceptionResolver, times(1))
                .resolveException(request, response, null, exception);
    }
}
