package com.kezul.backend.auth.adapter.in.web;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.junit.jupiter.api.BeforeEach;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kezul.backend.auth.adapter.in.web.dto.LogoutRequest;
import com.kezul.backend.auth.adapter.in.web.dto.TokenReissueRequest;
import com.kezul.backend.auth.application.port.in.LogoutUseCase;
import com.kezul.backend.auth.application.port.in.TokenReissueUseCase;
import com.kezul.backend.auth.application.port.in.dto.TokenReissueCommand;
import com.kezul.backend.auth.application.port.out.dto.TokenPair;

import com.kezul.backend.IntegrationTest;

@IntegrationTest
class AuthControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    @MockitoBean
    private TokenReissueUseCase tokenReissueUseCase;

    @MockitoBean
    private LogoutUseCase logoutUseCase;

    @Test
    @DisplayName("토큰 재발급 API 성공 테스트")
    void reissue_Success() throws Exception {
        // given
        TokenReissueRequest request = new TokenReissueRequest("valid-refresh-token", "iPhone");
        TokenPair mockNewTokens = new TokenPair("new-access-token", "new-refresh-token");

        given(tokenReissueUseCase.reissue(any(TokenReissueCommand.class))).willReturn(mockNewTokens);

        // when & then
        mockMvc.perform(
                post("/api/v1/auth/reissue")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("new-access-token"))
                .andExpect(jsonPath("$.data.refreshToken").value("new-refresh-token"));
    }

    @Test
    @DisplayName("로그아웃 API 성공 테스트")
    void logout_Success() throws Exception {
        // given
        LogoutRequest request = new LogoutRequest("valid-refresh-token");
        willDoNothing().given(logoutUseCase).logout(anyString());

        // when & then
        mockMvc.perform(
                post("/api/v1/auth/logout")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("잘못된 필드 값 (Validation 실패) 시 400 에러 반환 (추후 예외처리 적용 전엔 기본 스프링 오류 반환)")
    void reissue_Fail_Validation_EmptyToken() throws Exception {
        // given: 빈 리프레시 토큰
        TokenReissueRequest request = new TokenReissueRequest("", "iPhone");

        // when & then
        // 현재 GlobalExceptionHandler가 완벽히 없는 상태에서는 400 BadRequest가 떨어지는지 검증.
        mockMvc.perform(
                post("/api/v1/auth/reissue")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
