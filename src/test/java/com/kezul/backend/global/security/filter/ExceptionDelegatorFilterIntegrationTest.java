package com.kezul.backend.global.security.filter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.kezul.backend.IntegrationTest;
import com.kezul.backend.auth.exception.AuthErrorCode;
import com.kezul.backend.auth.exception.AuthException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * ExceptionDelegatorFilter → GlobalExceptionHandler 위임 흐름 통합 테스트.
 *
 * <p>
 * 전체 Spring Context 가 올라간 상태에서 테스트 전용 FilterChain 을 {@code @Order(1)} 로 최우선
 * 등록합니다.
 * 이 체인 안에 AlwaysThrowingFilter(AuthException 발생) 를 주입하여,
 * ExceptionDelegatorFilter 가 예외를 GlobalExceptionHandler 로 위임하는 흐름을 검증합니다.
 */
@IntegrationTest
@Import(ExceptionDelegatorFilterIntegrationTest.TestFilterConfig.class)
class ExceptionDelegatorFilterIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    @DisplayName("필터에서 AuthException(EXPIRED_TOKEN)이 발생하면 ExceptionDelegatorFilter 가 GlobalExceptionHandler 로 위임하여 401 응답과 에러 코드를 반환한다")
    void shouldReturn401WhenFilterThrowsExpiredTokenException() throws Exception {
        mockMvc.perform(get("/test/filter-error"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(AuthErrorCode.EXPIRED_TOKEN.getCode()));
    }

    @TestConfiguration
    static class TestFilterConfig {

        /**
         * 메인 SecurityConfig 의 FilterChain 보다 먼저 매칭되도록 @Order(1) 부여.
         * AlwaysThrowingFilter 를 삽입하여 필터 예외 위임 흐름을 재현합니다.
         *
         * <p>
         * Spring Security 5.8+ 는 두 체인이 동일한 요청에 모두 매칭될 때
         * UnreachableFilterChainException 을 냅니다.
         * 이를 피하기 위해 이 체인은 '/test/**' 경로에만 적용하고,
         * 메인 체인이 해당 경로를 처리하지 않도록 합니다.
         * (메인 SecurityConfig 는 그대로 두고, 이 체인이 우선 매칭됩니다.)
         */
        @Bean
        @Order(1)
        public SecurityFilterChain testFilterChain(
                HttpSecurity http,
                ExceptionDelegatorFilter exceptionDelegatorFilter)
                throws Exception {
            return http
                    .securityMatcher("/test/**")
                    .csrf(AbstractHttpConfigurer::disable)
                    .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                    .addFilterBefore(exceptionDelegatorFilter, LogoutFilter.class)
                    .addFilterBefore(new AlwaysThrowingFilter(), LogoutFilter.class)
                    .build();
        }
    }

    /**
     * 항상 AuthException(EXPIRED_TOKEN) 을 던지는 테스트 전용 필터.
     * JwtAuthenticationFilter 에서 만료된 토큰을 검증할 때와 동일한 상황을 시뮬레이션합니다.
     */
    static class AlwaysThrowingFilter extends OncePerRequestFilter {
        @Override
        protected void doFilterInternal(
                HttpServletRequest request,
                HttpServletResponse response,
                FilterChain filterChain) {
            throw new AuthException(AuthErrorCode.EXPIRED_TOKEN);
        }
    }
}
