package com.kezul.backend.auth.adapter.in.web.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.kezul.backend.user.domain.model.enums.OauthProvider;

/**
 * 문자열 → OauthProvider Enum 자동 변환 컨버터.
 * 대소문자를 구분하지 않으며, 일치하는 값이 없으면 400 Bad Request로 처리됩니다.
 */
@Component
public class OauthProviderConverter implements Converter<String, OauthProvider> {

    @Override
    public OauthProvider convert(String source) {
        return OauthProvider.valueOf(source.toUpperCase());
    }
}
