package com.PBL.lab.grade.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProgressResponse {
    // 진행상황 전용 필드들
    @JsonProperty("total_test_case")
    private Integer totalTestCase;

    @JsonProperty("done_test_case")
    private Integer doneTestCase;

    @JsonProperty("current_test_case")
    private Integer currentTestCase;

    @JsonProperty("progress_percentage")
    private Double progressPercentage;

    @JsonProperty("estimated_remaining_time")
    private Long estimatedRemainingTime; // milliseconds

    /**
     * 진행률 자동 계산
     */
    public void calculateProgress() {
        if (totalTestCase != null && totalTestCase > 0) {
            this.progressPercentage = (double) doneTestCase / totalTestCase * 100.0;
        } else {
            this.progressPercentage = 0.0;
        }
    }

    /**
     * 진행상황 업데이트 (편의 메서드)
     */
    public void updateProgress(int done, int total) {
        this.doneTestCase = done;
        this.totalTestCase = total;
        this.currentTestCase = done + 1; // 다음 테스트케이스
        calculateProgress();
    }
}
