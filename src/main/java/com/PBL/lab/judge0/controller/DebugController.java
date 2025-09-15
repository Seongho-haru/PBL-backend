package com.PBL.lab.judge0.controller;

import lombok.extern.slf4j.Slf4j;
import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.server.BackgroundJobServer;
import org.jobrunr.storage.StorageProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Slf4j
public class DebugController {

    @Autowired(required = false)
    private JobScheduler jobScheduler;
    
    @Autowired(required = false) 
    private BackgroundJobServer backgroundJobServer;
    
    @Autowired(required = false)
    private StorageProvider storageProvider;

    @GetMapping("/debug/jobrunr")
    public ResponseEntity<?> jobrunrStatus() {
        try {
            Map<String, Object> status = Map.of(
                "jobScheduler", jobScheduler != null ? "initialized" : "missing",
                "backgroundJobServer", backgroundJobServer != null ? "initialized" : "missing",
                "storageProvider", storageProvider != null ? "initialized" : "missing"
            );
            
            log.info("JobRunr Status: {}", status);
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            log.error("Error checking JobRunr status", e);
            return ResponseEntity.ok(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/debug/test-job")
    public ResponseEntity<?> testJob() {
        try {
            if (jobScheduler == null) {
                return ResponseEntity.ok(Map.of("error", "JobScheduler not available"));
            }
            
            String jobId = jobScheduler.enqueue(() -> {
                log.info("Test job is running!");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                log.info("Test job completed!");
            }).toString();
            
            return ResponseEntity.ok(Map.of("message", "Test job scheduled", "jobId", jobId));
        } catch (Exception e) {
            log.error("Error scheduling test job", e);
            return ResponseEntity.ok(Map.of("error", e.getMessage()));
        }
    }
}
