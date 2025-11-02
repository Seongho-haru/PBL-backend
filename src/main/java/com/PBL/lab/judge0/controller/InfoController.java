package com.PBL.lab.judge0.controller;

import com.PBL.lab.core.service.ConfigAggregatorService;
import com.PBL.lab.core.service.LanguageService;
import com.PBL.lab.judge0.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Info Controller
 *
 * System information and configuration endpoints.
 * Complete port of Judge0 Ruby InfoController.
 */
@RestController
@RequiredArgsConstructor
public class InfoController {

    private final ConfigAggregatorService configAggregatorService;
    private final SubmissionService submissionService;
    private final LanguageService languageService;

    /**
     * GET /system_info
     * Get system information
     */
    @GetMapping("/system_info")
    public ResponseEntity<Map<String, Object>> systemInfo() {
        try {
            Runtime runtime = Runtime.getRuntime();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory - freeMemory;
            
            Map<String, Object> response = Map.of(
                "hostname", getHostname(),
                "os", Map.of(
                    "name", System.getProperty("os.name"),
                    "version", System.getProperty("os.version"),
                    "arch", System.getProperty("os.arch")
                ),
                "java", Map.of(
                    "version", System.getProperty("java.version"),
                    "vendor", System.getProperty("java.vendor"),
                    "runtime", System.getProperty("java.runtime.name")
                ),
                "memory", Map.of(
                    "total", totalMemory,
                    "used", usedMemory,
                    "free", freeMemory,
                    "max", runtime.maxMemory()
                ),
                "processors", runtime.availableProcessors(),
                "uptime", getUptime()
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to get system info: " + e.getMessage()));
        }
    }

    /**
     * GET /config_info
     * Get configuration information
     */
    @GetMapping("/config_info")
    public ResponseEntity<Map<String, Object>> configInfo() {
        try {
            Map<String, Object> response = configAggregatorService.getConfigInfo();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to get config info: " + e.getMessage()));
        }
    }

    /**
     * GET /statistics
     * Get system statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> statistics() {
        try {
            Map<String, Object> submissionStats = submissionService.getSubmissionStatistics();
            Map<String, Object> languageStats = languageService.getLanguageStatistics();
            
            Map<String, Object> response = Map.of(
                "submissions", submissionStats,
                "languages", languageStats,
                "system", Map.of(
                    "uptime", getUptime(),
                    "memory_usage", getMemoryUsagePercentage(),
                    "cpu_count", Runtime.getRuntime().availableProcessors()
                )
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to get statistics: " + e.getMessage()));
        }
    }

    /**
     * GET /about
     * Get information about Judge0
     */
    @GetMapping("/about")
    public ResponseEntity<Map<String, Object>> about() {
        Map<String, Object> response = Map.of(
            "name", "Judge0 Spring",
            "version", "1.13.1",
            "description", "Judge0 is a robust and scalable open-source online code execution system",
            "homepage", "https://judge0.com",
            "source_code", "https://github.com/judge0/judge0-spring",
            "maintainer", "Judge0 Spring Team",
            "license", "MIT License",
            "platform", "Java Spring Boot",
            "original", Map.of(
                "name", "Judge0",
                "version", "1.13.1",
                "source", "https://github.com/judge0/judge0",
                "maintainer", "Herman Zvonimir Došilović"
            )
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * GET /version
     * Get version information
     */
    @GetMapping("/version")
    public ResponseEntity<Map<String, Object>> version() {
        Map<String, Object> response = Map.of(
            "version", "1.13.1",
            "commit", getCommitHash(),
            "build_date", getBuildDate(),
            "platform", "Java Spring Boot"
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * GET /license
     * Get license information
     */
    @GetMapping("/license")
    public ResponseEntity<Map<String, Object>> license() {
        Map<String, Object> response = Map.of(
            "license", "MIT License",
            "url", "https://opensource.org/licenses/MIT",
            "description", "Permission is hereby granted, free of charge, to any person obtaining a copy...",
            "notice", "This software is provided 'as is', without warranty of any kind"
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * GET /isolate
     * Get isolate/container information
     */
    @GetMapping("/isolate")
    public ResponseEntity<Map<String, Object>> isolate() {
        Map<String, Object> response = Map.of(
            "sandbox", "Docker Container",
            "isolation_method", "Container Namespaces + cgroups",
            "security_features", Map.of(
                "network_isolation", true,
                "filesystem_isolation", true,
                "process_isolation", true,
                "memory_limits", true,
                "cpu_limits", true,
                "time_limits", true
            ),
            "equivalent_to", "Isolate sandbox from original Judge0",
            "docker_features", Map.of(
                "readonly_filesystem", true,
                "no_new_privileges", true,
                "dropped_capabilities", true,
                "user_namespace", true
            )
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get hostname
     */
    private String getHostname() {
        try {
            return java.net.InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "unknown";
        }
    }

    /**
     * Get uptime in milliseconds
     */
    private long getUptime() {
        return java.lang.management.ManagementFactory.getRuntimeMXBean().getUptime();
    }

    /**
     * Get memory usage percentage
     */
    private double getMemoryUsagePercentage() {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        return (double) usedMemory / totalMemory * 100.0;
    }

    /**
     * Get commit hash (from build info)
     */
    private String getCommitHash() {
        // In a real build, this would be injected from git info
        return "unknown";
    }

    /**
     * Get build date
     */
    private String getBuildDate() {
        // In a real build, this would be injected from build timestamp
        return "unknown";
    }
}
