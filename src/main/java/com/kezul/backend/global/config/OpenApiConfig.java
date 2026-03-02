package com.kezul.backend.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger UI 전역 보안 및 기본 정보 설정.
 * 모든 API에 기본적으로 JWT 인증을 요구하며, 서버 환경별 접근 URL을 제공한다.
 * (인증 불필요 API는 컨트롤러의 메서드에 @SecurityRequirements()로 개별 오버라이드)
 */
@Configuration
public class OpenApiConfig {

        @Bean
        public OpenAPI openAPI() {
                SecurityScheme securityScheme = new SecurityScheme()
                                .name("JWT")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT");

                SecurityRequirement securityRequirement = new SecurityRequirement().addList("JWT");

                Components components = new Components()
                                .addSecuritySchemes("JWT", securityScheme);

                List<Server> servers = List.of(
                                new Server().url("http://localhost:8080").description("로컬 서버"),
                                new Server().url("https://api-dev.kezul.com").description("개발(Dev) 서버"));

                return new OpenAPI()
                                .info(new Info()
                                                .title("Kezul API 명세서")
                                                .description("Kezul 웹/앱 프론트엔드 연동을 위한 백엔드 REST API 문서입니다.")
                                                .version("v1.0.0"))
                                .servers(servers)
                                .addSecurityItem(securityRequirement)
                                .components(components);
        }
}
