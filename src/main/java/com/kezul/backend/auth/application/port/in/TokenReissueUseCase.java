package com.kezul.backend.auth.application.port.in;

import com.kezul.backend.auth.application.port.in.dto.TokenReissueCommand;
import com.kezul.backend.auth.application.port.out.dto.TokenPair;

public interface TokenReissueUseCase {
    /**
     * Refresh Token을 이용하여 새로운 Access Token과 Refresh Token 쌍을 발급받습니다. (Refresh Token
     * Rotation)
     */
    TokenPair reissue(TokenReissueCommand command);
}
