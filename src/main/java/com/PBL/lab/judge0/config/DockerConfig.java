package com.PBL.lab.judge0.config;

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
 * Docker Configuration
 * 
 * Configures Docker client and related services for code execution.
 */
@Configuration
@Slf4j
public class DockerConfig {

    @Value("${judge0.docker.host:unix:///var/run/docker.sock}")
    private String dockerHost;

    @Value("${judge0.docker.registry-url:docker.io}")
    private String registryUrl;

    @Value("${judge0.docker.max-concurrent-containers:10}")
    private Integer maxConcurrentContainers;

    @Value("${judge0.docker.container-timeout:300}")
    private Integer containerTimeoutSeconds;

    @Value("${judge0.docker.api-version:}")
    private String apiVersion;

    @Value("${judge0.docker.tls-verify:false}")
    private Boolean tlsVerify;

    @Value("${judge0.docker.cert-path:}")
    private String certPath;

    /**
     * Configure Docker client
     */
    @Bean
    public DockerClient dockerClient() {
        try {
            log.info("Initializing Docker client with host: {}", dockerHost);

            DefaultDockerClientConfig.Builder configBuilder = DefaultDockerClientConfig.createDefaultConfigBuilder()
                    .withDockerHost(dockerHost)
                    .withRegistryUrl(registryUrl);

            if (apiVersion != null && !apiVersion.trim().isEmpty()) {
                configBuilder.withApiVersion(apiVersion);
            }

            if (Boolean.TRUE.equals(tlsVerify)) {
                configBuilder.withDockerTlsVerify(true);
                if (certPath != null && !certPath.trim().isEmpty()) {
                    configBuilder.withDockerCertPath(certPath);
                }
            }

            DefaultDockerClientConfig config = configBuilder.build();

            ApacheDockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                    .dockerHost(config.getDockerHost())
                    .sslConfig(config.getSSLConfig())
                    .maxConnections(maxConcurrentContainers)
                    .connectionTimeout(Duration.ofSeconds(30))
                    .responseTimeout(Duration.ofSeconds(containerTimeoutSeconds))
                    .build();

            DockerClient dockerClient = DockerClientImpl.getInstance(config, httpClient);

            // Test Docker connection
            testDockerConnection(dockerClient);

            log.info("Docker client initialized successfully");
            return dockerClient;

        } catch (Exception e) {
            log.error("Failed to initialize Docker client", e);
            throw new RuntimeException("Docker client initialization failed", e);
        }
    }

    /**
     * Test Docker connection
     */
    private void testDockerConnection(DockerClient dockerClient) {
        try {
            var info = dockerClient.infoCmd().exec();
            log.info("Docker connection successful - Version: {}, Containers: {}", 
                    info.getServerVersion(), info.getContainers());
        } catch (Exception e) {
            log.error("Docker connection test failed", e);
            throw new RuntimeException("Cannot connect to Docker daemon", e);
        }
    }

    /**
     * Docker configuration properties
     */
    @Bean
    public DockerProperties dockerProperties() {
        return DockerProperties.builder()
                .host(dockerHost)
                .registryUrl(registryUrl)
                .maxConcurrentContainers(maxConcurrentContainers)
                .containerTimeoutSeconds(containerTimeoutSeconds)
                .apiVersion(apiVersion)
                .tlsVerify(tlsVerify)
                .certPath(certPath)
                .build();
    }

    /**
     * Docker properties data class
     */
    @lombok.Data
    @lombok.Builder
    public static class DockerProperties {
        private String host;
        private String registryUrl;
        private Integer maxConcurrentContainers;
        private Integer containerTimeoutSeconds;
        private String apiVersion;
        private Boolean tlsVerify;
        private String certPath;
    }
}
