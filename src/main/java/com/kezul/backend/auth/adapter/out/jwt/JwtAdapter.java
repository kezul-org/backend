package com.kezul.backend.auth.adapter.out.jwt;

import java.text.ParseException;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.kezul.backend.auth.application.port.out.JwtPort;
import com.kezul.backend.auth.application.port.out.dto.TokenPair;
import com.kezul.backend.auth.config.JwtProperties;
import com.kezul.backend.global.error.AppException;
import com.kezul.backend.global.error.ErrorCode;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtAdapter implements JwtPort {

    private final JwtProperties jwtProperties;
    private final Clock clock;
    private final JWSSigner signer;
    private final JWSVerifier verifier;

    public JwtAdapter(JwtProperties jwtProperties, Clock clock) {
        this.jwtProperties = jwtProperties;
        this.clock = clock;
        try {
            // HS256 requires a secret key of at least 256 bits (32 bytes)
            this.signer = new MACSigner(jwtProperties.getSecretKey());
            this.verifier = new MACVerifier(jwtProperties.getSecretKey());
        } catch (JOSEException e) {
            throw new IllegalStateException("Failed to initialize JWT signer/verifier with provided secret key", e);
        }
    }

    @Override
    public TokenPair generateTokenPair(Long userId, String role) {
        String accessToken = generateToken(
                userId,
                role,
                jwtProperties.getAccessTokenExpirationMinutes(),
                ChronoUnit.MINUTES,
                "access");
        String refreshToken = generateToken(
                userId,
                role,
                jwtProperties.getRefreshTokenExpirationDays(),
                ChronoUnit.DAYS,
                "refresh");

        return TokenPair.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private String generateToken(Long userId, String role, long expirationAmount, ChronoUnit unit, String type) {
        Instant now = clock.instant();
        Instant expiration = now.plus(expirationAmount, unit);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .issuer(jwtProperties.getIssuer())
                .subject(userId.toString())
                .claim("role", role)
                .claim("type", type)
                .issueTime(Date.from(now))
                .expirationTime(Date.from(expiration))
                .build();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);

        try {
            signedJWT.sign(signer);
            return signedJWT.serialize();
        } catch (JOSEException e) {
            log.error("Failed to sign '{}' JWT for user {}", type, userId, e);
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public boolean validateToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            if (!signedJWT.verify(verifier)) {
                log.debug("JWT verification failed");
                return false;
            }
            Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            if (expirationTime == null) {
                return false;
            }
            // token has expired -> Date.from(clock.instant()) is after expirationTime
            if (expirationTime.before(Date.from(clock.instant()))) {
                log.debug("JWT token expired");
                return false;
            }
            return true;
        } catch (ParseException | JOSEException e) {
            log.debug("Invalid JWT token format: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public Long getUserId(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            verifyTokenSignature(signedJWT);
            return Long.parseLong(signedJWT.getJWTClaimsSet().getSubject());
        } catch (ParseException e) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
    }

    @Override
    public String getRole(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            verifyTokenSignature(signedJWT);
            return signedJWT.getJWTClaimsSet().getStringClaim("role");
        } catch (ParseException e) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
    }

    @Override
    public Instant getExpirationTime(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            verifyTokenSignature(signedJWT);
            Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            if (expirationTime == null) {
                throw new AppException(ErrorCode.INVALID_TOKEN);
            }
            return expirationTime.toInstant();
        } catch (ParseException e) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
    }

    private void verifyTokenSignature(SignedJWT signedJWT) {
        try {
            if (!signedJWT.verify(verifier)) {
                throw new AppException(ErrorCode.INVALID_TOKEN);
            }
        } catch (JOSEException e) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
    }
}
