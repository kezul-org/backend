/**
 * Auth 도메인 모듈.
 * JWT 기반 인증/인가, 토큰 발급 및 갱신 기능을 담당합니다.
 * user 모듈의 내부 구현에 직접 접근하지 않으며, 이벤트/공개 API를 통해서만 협력합니다.
 */
@org.springframework.modulith.ApplicationModule
package com.kezul.backend.auth;
