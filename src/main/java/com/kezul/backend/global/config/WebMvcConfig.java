package com.kezul.backend.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.kezul.backend.auth.adapter.in.web.converter.OauthProviderConverter;

import lombok.RequiredArgsConstructor;

/**
 * 전역 웹(MVC) 설정 클래스.
 * Converter 등록, CORS 빈 설정 등을 담당합니다.
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final OauthProviderConverter oauthProviderConverter;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(oauthProviderConverter);
    }
}
