package com.PBL.lab.judge0.config;

import lombok.extern.slf4j.Slf4j;
import org.jobrunr.configuration.JobRunr;
import org.jobrunr.storage.InMemoryStorageProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * JobRunr 설정 클래스
 * 
 * 이 클래스는 Judge0 시스템에서 백그라운드 작업 처리를 위한 JobRunr 라이브러리를 설정합니다.
 * JobRunr는 안정적이고 신뢰할 수 있는 백그라운드 작업 처리를 제공하는 Java 라이브러리입니다.
 * 
 * 주요 설정 내용:
 * - InMemoryStorageProvider: 작업 정보를 메모리에 저장 (개발/테스트 환경용)
 * - BackgroundJobServer: 백그라운드 작업을 처리하는 워커 서버 활성화
 * - 자동 초기화: Spring Boot 애플리케이션 시작 시 JobRunr 자동 초기화
 * 
 * JobRunr의 장점:
 * - 분산 환경에서 안정적인 작업 처리
 * - 작업 실패 시 자동 재시도 기능
 * - 작업 상태 추적 및 모니터링
 * - 데이터베이스 기반 작업 영속성
 * - 웹 대시보드를 통한 작업 모니터링
 * 
 * 사용 목적:
 * - 코드 실행 작업의 비동기 처리
 * - 대용량 코드 제출의 큐 관리
 * - 시스템 부하 분산 및 안정성 확보
 * - 작업 실패 시 자동 복구
 */
@Configuration
@Slf4j
public class JobRunrConfig {

    /**
     * JobRunr 라이브러리 초기화 Bean
     * 
     * 이 메서드는 Spring Boot 애플리케이션 시작 시 JobRunr를 초기화합니다.
     * @Primary 어노테이션으로 다른 JobRunr 설정보다 우선순위를 가집니다.
     * 
     * 초기화 과정:
     * 1. InMemoryStorageProvider 설정: 작업 정보를 메모리에 저장
     * 2. BackgroundJobServer 활성화: 백그라운드 작업 처리 워커 시작
     * 3. JobRunr 초기화: 모든 설정을 적용하고 서버 시작
     * 
     * 스토리지 옵션:
     * - InMemoryStorageProvider: 개발/테스트 환경용 (애플리케이션 재시작 시 데이터 손실)
     * - SQLStorageProvider: 운영 환경용 (데이터베이스에 작업 정보 영구 저장)
     * 
     * 워커 서버 기능:
     * - 등록된 작업들을 큐에서 순차적으로 처리
     * - 작업 실패 시 자동 재시도 (기본 3회)
     * - 작업 상태 실시간 업데이트
     * - 시스템 리소스 모니터링
     * 
     * @return String 초기화 완료 메시지 (Bean 이름으로 사용)
     */
    @Bean
    @Primary
    public String initializeJobRunr() {
        log.info("JobRunr 백그라운드 작업 서버 초기화 시작");

        // JobRunr 라이브러리 설정 및 초기화
        // 이 과정에서 백그라운드 작업 처리에 필요한 모든 컴포넌트가 설정됨
        JobRunr.configure()
                // 메모리 기반 스토리지 프로바이더 설정
                // 개발/테스트 환경에서는 메모리 저장소가 충분하지만,
                // 운영 환경에서는 SQLStorageProvider 사용 권장
                .useStorageProvider(new InMemoryStorageProvider())
                // 백그라운드 작업 처리 워커 서버 활성화
                // 이 설정으로 JobRunr가 등록된 작업들을 자동으로 처리함
                .useBackgroundJobServer()
                // 모든 설정을 적용하고 JobRunr 서버 초기화 및 시작
                .initialize();

        log.info("JobRunr 백그라운드 작업 서버 초기화 완료");

        // Spring Bean 이름으로 사용될 문자열 반환
        return "JobRunr-Initialized";
    }
}
