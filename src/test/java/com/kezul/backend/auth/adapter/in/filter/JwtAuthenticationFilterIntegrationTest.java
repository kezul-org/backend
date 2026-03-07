package com.kezul.backend.auth.adapter.in.filter;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.kezul.backend.IntegrationTest;
import com.kezul.backend.auth.application.port.out.JwtPort;
import com.kezul.backend.auth.application.port.out.dto.TokenPair;
import com.kezul.backend.auth.exception.AuthErrorCode;
import com.kezul.backend.global.security.configurer.DomainSecurityConfigurer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

/**
 * JwtAuthenticationFilter 통합 테스트.
 *
 * <p>
 * 전체 Spring Context를 올리고, 테스트 전용 컨트롤러({@code /test/jwt-resource})를 등록하여
 * 토큰 유/무/위조에 따른 필터 동작을 검증합니다.
 *
 * <p>
 * 테스트 엔드포인트는 메인 SecurityConfig의 {@code anyRequest().authenticated()} 범위에 포함되므로
 * 별도 SecurityFilterChain 설정 없이 실제 JwtAuthenticationFilter 동작을 검증합니다.
 */
@IntegrationTest
@Import(JwtAuthenticationFilterIntegrationTest.TestControllerConfig.class)
class JwtAuthenticationFilterIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private JwtPort jwtPort;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Authorization 헤더 없이 요청하면 필터를 통과하지만 Spring Security가 403을 반환한다")
    void 토큰이_없으면_403을_반환한다() throws Exception {
        mockMvc.perform(get("/test/jwt-resource"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("유효하지 않은 토큰으로 요청하면 JwtAuthenticationFilter가 INVALID_TOKEN 에러를 던지고 401을 반환한다")
    void 유효하지_않은_토큰이면_401을_반환한다() throws Exception {
        mockMvc.perform(
                get("/test/jwt-resource")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer invalid.token.value"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(AuthErrorCode.INVALID_TOKEN.getCode()));
    }

    @Test
    @DisplayName("유효한 토큰으로 요청하면 SecurityContext에 인증 정보가 세팅되어 200을 반환한다")
    void 유효한_토큰이면_인증에_성공하고_200을_반환한다() throws Exception {
        Long userId = 1L;
        String role = "USER";
        TokenPair tokenPair = jwtPort.generateTokenPair(userId, role);

        mockMvc.perform(
                get("/test/jwt-resource")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenPair.accessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(userId.toString()));
    }

    /**
     * 테스트 전용 컨트롤러 및 보안 configurer.
     *
     * <p>
     * {@code /test/jwt-resource}는 메인 SecurityConfig의 anyRequest().authenticated()
     * 범위에
     * 포함되어 있으므로, JwtAuthenticationFilter가 토큰을 검증해 SecurityContext를 채워야 200을 반환합니다.
     */
    @TestConfiguration
    static class TestControllerConfig {

        /**
         * 테스트 컨트롤러를 명시적으로 Bean 으로 등록.
         * (컴포넌트 스캔 대상이 아니므로 직접 등록합니다.)
         */
        @Bean
        public JwtResourceController jwtResourceController() {
            return new JwtResourceController();
        }

        /**
         * /test/jwt-resource 요청을 허용된 범위에 포함시키기 위한 configurer.
         * SecurityConfig가 List<DomainSecurityConfigurer>를 수집할 때 이 Bean도 포함됩니다.
         *
         * <p>
         * 별도 규칙 없이 메인 SecurityConfig의 anyRequest().authenticated()로 처리하므로
         * 빈 구현으로 두고, 실질적 인증 검증은 JwtAuthenticationFilter에서 수행됩니다.
         */
        @Bean
        public DomainSecurityConfigurer testJwtDomainConfigurer() {
            return authorize -> {
            };
        }
    }

    @RestController
    static class JwtResourceController {

        /**
         * SecurityContext의 Principal(userId)을 응답으로 반환.
         * 유효한 토큰이 있어야만 인증이 통과되어 이 메서드까지 도달합니다.
         */
        @GetMapping("/test/jwt-resource")
        public ResponseEntity<String> resource(
                org.springframework.security.core.Authentication authentication) {
            return ResponseEntity.ok(authentication.getPrincipal().toString());
        }
    }
}
