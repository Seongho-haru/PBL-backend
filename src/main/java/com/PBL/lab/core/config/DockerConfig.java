package com.PBL.lab.core.config;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Docker 설정 클래스 - Judge0 코드 실행을 위한 Docker 클라이언트 및 관련 서비스 구성
 * 
 * 목적:
 * - Docker 클라이언트 초기화 및 연결 설정
 * - Judge0 코드 실행 환경을 위한 Docker 인프라 구성
 * - 컨테이너 실행 제한 및 보안 설정 관리
 * 
 * 핵심 기능:
 * - Docker 데몬 연결 설정 (Unix 소켓 또는 TCP)
 * - Docker API 버전 및 TLS 보안 설정
 * - 동시 컨테이너 수 제한 및 타임아웃 설정
 * - Docker 연결 테스트 및 상태 확인
 * 
 * 보안 고려사항:
 * - TLS 인증서 기반 보안 연결 지원
 * - 컨테이너 실행 시간 제한
 * - 동시 실행 컨테이너 수 제한으로 리소스 보호
 */
@Configuration
@Slf4j
public class DockerConfig {

    // Docker 설정값들 - application.yml에서 주입받는 Docker 관련 구성 파라미터
    @Value("${judge0.docker.host:unix:///var/run/docker.sock}")
    private String dockerHost; // Docker 데몬 호스트 주소 (기본: Unix 소켓)

    @Value("${judge0.docker.registry-url:docker.io}")
    private String registryUrl; // Docker 레지스트리 URL (기본: Docker Hub)

    @Value("${judge0.docker.max-concurrent-containers:10}")
    private Integer maxConcurrentContainers; // 최대 동시 실행 컨테이너 수

    @Value("${judge0.docker.container-timeout:300}")
    private Integer containerTimeoutSeconds; // 컨테이너 타임아웃 (초)

    @Value("${judge0.docker.api-version:}")
    private String apiVersion; // Docker API 버전 (빈 문자열 시 자동 감지)

    @Value("${judge0.docker.tls-verify:false}")
    private Boolean tlsVerify; // TLS 인증서 검증 여부

    @Value("${judge0.docker.cert-path:}")
    private String certPath; // TLS 인증서 경로

    /**
     * Docker 클라이언트 빈 구성 - Judge0 코드 실행을 위한 Docker 클라이언트 생성 및 설정
     * @return 구성된 DockerClient 인스턴스
     */
    @Bean
    public DockerClient dockerClient() {
        try {
            log.info("Initializing Docker client with host: {}", dockerHost);

            // Docker 클라이언트 기본 설정 구성
            DefaultDockerClientConfig.Builder configBuilder = DefaultDockerClientConfig.createDefaultConfigBuilder()
                    .withDockerHost(dockerHost) // Docker 데몬 호스트 설정
                    .withRegistryUrl(registryUrl); // Docker 레지스트리 URL 설정

            // API 버전이 지정된 경우 설정
            if (apiVersion != null && !apiVersion.trim().isEmpty()) {
                configBuilder.withApiVersion(apiVersion);
            }

            // TLS 인증서 검증이 활성화된 경우 보안 설정 적용
            if (Boolean.TRUE.equals(tlsVerify)) {
                configBuilder.withDockerTlsVerify(true); // TLS 검증 활성화
                if (certPath != null && !certPath.trim().isEmpty()) {
                    configBuilder.withDockerCertPath(certPath); // 인증서 경로 설정
                }
            }

            DefaultDockerClientConfig config = configBuilder.build();

            // Apache HTTP 클라이언트로 Docker API 통신 설정
            ApacheDockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                    .dockerHost(config.getDockerHost()) // Docker 호스트 설정
                    .sslConfig(config.getSSLConfig()) // SSL 설정 적용
                    .maxConnections(maxConcurrentContainers) // 최대 동시 연결 수 제한
                    .connectionTimeout(Duration.ofSeconds(30)) // 연결 타임아웃 30초
                    .responseTimeout(Duration.ofSeconds(containerTimeoutSeconds)) // 응답 타임아웃 설정
                    .build();

            // Docker 클라이언트 인스턴스 생성
            DockerClient dockerClient = DockerClientImpl.getInstance(config, httpClient);

            // Docker 연결 테스트 실행
            testDockerConnection(dockerClient);

            log.info("Docker client initialized successfully");
            return dockerClient;

        } catch (Exception e) {
            log.error("Failed to initialize Docker client", e);
            throw new RuntimeException("Docker client initialization failed", e);
        }
    }

    /**
     * Docker 연결 테스트 - Docker 데몬과의 연결 상태 확인 및 정보 수집
     * @param dockerClient 테스트할 Docker 클라이언트
     */
    private void testDockerConnection(DockerClient dockerClient) {
        try {
            // Docker 데몬 정보 조회를 통한 연결 상태 확인
            var info = dockerClient.infoCmd().exec();
            log.info("Docker connection successful - Version: {}, Containers: {}", 
                    info.getServerVersion(), info.getContainers());
        } catch (Exception e) {
            log.error("Docker connection test failed", e);
            throw new RuntimeException("Cannot connect to Docker daemon", e);
        }
    }

    /**
     * Docker 설정 속성 빈 구성 - 다른 컴포넌트에서 Docker 설정 정보를 주입받기 위한 빈
     * @return Docker 설정 속성 객체
     */
    @Bean
    public DockerProperties dockerProperties() {
        return DockerProperties.builder()
                .host(dockerHost) // Docker 호스트 주소
                .registryUrl(registryUrl) // Docker 레지스트리 URL
                .maxConcurrentContainers(maxConcurrentContainers) // 최대 동시 컨테이너 수
                .containerTimeoutSeconds(containerTimeoutSeconds) // 컨테이너 타임아웃
                .apiVersion(apiVersion) // Docker API 버전
                .tlsVerify(tlsVerify) // TLS 검증 여부
                .certPath(certPath) // TLS 인증서 경로
                .build();
    }

    /**
     * Docker 설정 속성 데이터 클래스 - Docker 관련 설정 정보를 담는 불변 객체
     * Lombok의 @Data와 @Builder를 사용하여 getter/setter와 빌더 패턴 제공
     */
    @lombok.Data
    @lombok.Builder
    public static class DockerProperties {
        private String host; // Docker 데몬 호스트 주소
        private String registryUrl; // Docker 레지스트리 URL
        private Integer maxConcurrentContainers; // 최대 동시 실행 컨테이너 수
        private Integer containerTimeoutSeconds; // 컨테이너 타임아웃 (초)
        private String apiVersion; // Docker API 버전
        private Boolean tlsVerify; // TLS 인증서 검증 여부
        private String certPath; // TLS 인증서 파일 경로
    }
}
