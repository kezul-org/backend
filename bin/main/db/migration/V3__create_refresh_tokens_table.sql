CREATE TABLE refresh_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token_value VARCHAR(512) NOT NULL,
    device_info VARCHAR(255),
    expires_at DATETIME(6) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    CONSTRAINT uk_token_value UNIQUE (token_value),
    INDEX idx_user_id (user_id)
);
