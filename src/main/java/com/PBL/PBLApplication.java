package com.PBL;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Judge0 Spring Boot Application - 메인 애플리케이션 엔트리 포인트
 * 
 * 목적:
 * - Judge0 온라인 저지 시스템의 Java Spring Boot 구현체
 * - Ruby on Rails 기반 원본 Judge0를 Java로 완전 포팅
 * - 다양한 프로그래밍 언어의 코드 컴파일 및 실행을 안전한 환경에서 처리
 * 
 * 핵심 기능:
 * - RESTful API를 통한 코드 제출 및 결과 조회
 * - Docker 컨테이너 기반 안전한 코드 실행 환경
 * - 비동기 작업 처리를 통한 고성능 코드 실행
 * - 캐싱을 통한 성능 최적화
 * - 다중 언어 지원 (C, C++, Java, Python, JavaScript 등)
 * 
 * 아키텍처 특징:
 * - @EnableCaching: 언어 정보, 제출 결과 등을 캐싱하여 성능 향상
 * - @EnableAsync: 코드 실행 작업을 비동기로 처리하여 응답성 개선
 * - Docker 컨테이너 풀링을 통한 리소스 효율적 관리
 * - JobRunr을 통한 안정적인 백그라운드 작업 처리
 * 
 * 원본 Judge0와의 호환성:
 * - 동일한 REST API 인터페이스 제공
 * - 같은 상태 코드 및 응답 형식 유지
 * - 기존 클라이언트와 100% 호환성 보장
 * 
 * @author Judge0 Spring Team
 * @version 1.13.1
 * @since 1.0.0
 */
@SpringBootApplication
@EnableCaching
@EnableAsync
@ComponentScan(basePackages = {
    "com.PBL.lab",        // 기존 Judge0 패키지
    "com.PBL.lecture",    // Lecture 패키지
    "com.PBL.curriculum", // Curriculum 패키지
    "com.PBL.user",       // User 패키지
    "com.PBL.s3",         // S3 패키지
    "com.PBL.qna"         // Q&A 패키지
})
public class PBLApplication {

    public static void main(String[] args) {
        SpringApplication.run(PBLApplication.class, args);
    }
}
