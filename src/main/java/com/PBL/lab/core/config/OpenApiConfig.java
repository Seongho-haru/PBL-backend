package com.PBL.lab.core.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger 설정 클래스
 * Judge0 Spring Boot API 문서화를 위한 설정
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Judge0 Spring Boot API")
                        .version("1.13.1")
                        .description("""
                                Judge0 Spring Boot는 온라인 코드 실행을 위한 강력한 API입니다.
                                
                                ## 주요 기능
                                - 다양한 프로그래밍 언어 지원 (C, C++, Java, Python, JavaScript 등)
                                - Docker 컨테이너 기반 안전한 코드 실행
                                - 강의 관리 시스템 (마크다운 강의 + 문제 강의)
                                - 자동 채점 시스템
                                - 실시간 진행상황 추적 (SSE)
                                
                                ## API 카테고리
                                - **Judge0 Core**: 기본 코드 실행 API
                                - **Lectures**: 강의 관리 API
                                - **Grading**: 자동 채점 API
                                - **System**: 시스템 정보 및 모니터링
                                """)
                        .contact(new Contact()
                                .name("Judge0 Spring Team")
                                .url("https://github.com/judge0/judge0")
                                .email("support@judge0.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:2358")
                                .description("Development Server"),
                        new Server()
                                .url("https://api.judge0.com")
                                .description("Production Server")
                ));
    }
}
