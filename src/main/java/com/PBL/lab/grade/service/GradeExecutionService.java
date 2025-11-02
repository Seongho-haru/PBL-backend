package com.PBL.lab.grade.service;

import com.PBL.lab.core.dto.ValidationResult;
import com.PBL.lab.core.service.BaseJobScheduler;
import com.PBL.lab.core.service.DockerExecutionService;
import com.PBL.lab.grade.entity.Grade;
import com.PBL.lab.core.enums.Status;
import com.PBL.lab.core.dto.ExecutionResult;
import com.PBL.lab.grade.job.GradeJob;
import lombok.extern.slf4j.Slf4j;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class GradeExecutionService extends BaseJobScheduler {
    private final GradeJob gradeJob;
    private final JobScheduler jobScheduler;// 실제 코드 실행을 담당하는 작업 클래스

    public GradeExecutionService(
            JobScheduler jobScheduler,
            GradeJob gradeJob
    ) {
        super(jobScheduler);
        this.gradeJob = gradeJob;
        this.jobScheduler = jobScheduler;
    }

    @Override
    public void executeJob(String token) {
        gradeJob.executeGrade(token);
    }
}
