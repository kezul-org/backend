package com.kezul.backend.global.config;

import com.kezul.backend.IntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@IntegrationTest
class I18nIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @DisplayName("Accept-Language: ko 헤더로 요청 시 한국어 에러 메시지를 반환한다")
    void shouldReturnKoreanMessageWhenAcceptLanguageIsKo() throws Exception {
        mockMvc.perform(get("/api/invalid-url")
                .header("Accept-Language", "ko"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("C003"))
                .andExpect(jsonPath("$.message").value("요청한 리소스를 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("Accept-Language: en 헤더로 요청 시 영어 에러 메시지를 반환한다")
    void shouldReturnEnglishMessageWhenAcceptLanguageIsEn() throws Exception {
        mockMvc.perform(get("/api/invalid-url")
                .header("Accept-Language", "en"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("C003"))
                .andExpect(jsonPath("$.message").value("Requested resource not found."));
    }

    @Test
    @DisplayName("Accept-Language 헤더가 없을 시 기본 한국어 에러 메시지를 반환한다")
    void shouldReturnDefaultKoreanMessageWithoutAcceptLanguageHeader() throws Exception {
        mockMvc.perform(get("/api/invalid-url"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("C003"))
                .andExpect(jsonPath("$.message").value("요청한 리소스를 찾을 수 없습니다."));
    }
}
