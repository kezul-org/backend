// <editor-fold defaultstate="collapsed" desc="[접어두기] TODO: (사용자 학습 1/5) Named Interface 선언">
// 전체 흐름: 도메인 계층의 핵심 열거형(Enum)을 다른 모듈에서 안전하게 참조할 수 있도록 API 계약서를 갱신하는 단계입니다.
// 왜? OauthProvider 등 공통 도메인 Enum을 외부 모듈(auth)에서 합법적으로 참조하도록 보호 범위를 해제합니다.
// 힌트: @org.springframework.modulith.NamedInterface("enums")
// </editor-fold>
@org.springframework.modulith.NamedInterface("enums")
package com.kezul.backend.user.domain.model.enums;
