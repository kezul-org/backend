CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    oauth_provider VARCHAR(20) NOT NULL COMMENT '소셜 로그인 제공자 (KAKAO, NAVER, APPLE, GOOGLE)',
    oauth_id VARCHAR(100) NOT NULL COMMENT '소셜 로그인 고유 식별자',
    email VARCHAR(255) COMMENT '사용자 이메일 주소',
    nickname VARCHAR(50) NOT NULL COMMENT '사용자 화면 표시 닉네임',
    profile_image_url VARCHAR(500) COMMENT '사용자 프로필 이미지 URL',
    role VARCHAR(20) NOT NULL COMMENT '사용자 권한 (USER, TRAINER, ADMIN)',
    created_at DATETIME(6) NOT NULL COMMENT '가입일시 (생성일시)',
    updated_at DATETIME(6) NOT NULL COMMENT '수정일시',
    UNIQUE KEY uk_oauth (oauth_provider, oauth_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='사용자 계정 및 프로필 통합 정보';

-- 빠른 조회를 위한 이메일 인덱스 (선택적)
CREATE INDEX idx_users_email ON users(email);
