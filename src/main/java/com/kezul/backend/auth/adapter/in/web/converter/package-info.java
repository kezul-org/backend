// <editor-fold defaultstate="collapsed" desc="[접어두기] TODO: (사용자 학습 1/5) Named Interface 선언">
// 전체 흐름: HTTP 요청 처리 시 Enum 변환기(Converter)를 다른 모듈(global)에서도 전역 등록할 수 있도록 허용하는 단계입니다.
// 왜? OauthProviderConverter를 global 모듈의 WebMvcConfig에서 등록에 사용하도록 허용합니다.
// 힌트: @org.springframework.modulith.NamedInterface("converter")
// </editor-fold>
@NamedInterface("converter")
package com.kezul.backend.auth.adapter.in.web.converter;

import org.springframework.modulith.NamedInterface;
