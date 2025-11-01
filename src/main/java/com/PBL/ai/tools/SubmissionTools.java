package com.PBL.ai.tools;

import com.PBL.lab.grading.dto.GradingResponse;
import com.PBL.lab.grading.entity.Grading;
import com.PBL.lab.grading.service.GradingService;
import com.PBL.lab.judge0.dto.SubmissionResponse;
import com.PBL.lab.judge0.entity.Submission;
import com.PBL.lab.judge0.service.SubmissionService;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * AI가 사용할 코드 실행 및 채점 도구
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SubmissionTools {

    private final SubmissionService submissionService;
    private final GradingService gradingService;

    // ========================================
    // 코드 실행 관련 도구
    // ========================================

    @Tool("제출 토큰으로 코드 실행 결과를 조회합니다. 실행 상태, 출력 결과, 에러 메시지, 실행 시간, 메모리 사용량 등을 확인할 수 있습니다.")
    public Submission getSubmissionByToken(@P("조회할 제출의 토큰") String submissionToken) {
        log.debug("🔧 [도구 호출] getSubmission - 파라미터: submissionToken={}", submissionToken);
        Submission result = submissionService.findByToken(submissionToken);
        log.debug("✅ [도구 결과] getSubmission - 상태: {}", result != null ? result.getStatus() : "null");
        return result;
    }

    @Tool("코드 제출 목록을 페이징으로 조회합니다. 실행 결과와 상태를 확인할 수 있습니다.")
    public Page<Submission> getSubmission(
            @P("페이지 번호 (1부터 시작)") int page,
            @P("페이지 크기 (기본값: 20)") int size,
            @P("코드를 Base64로 인코딩하여 반환할지 여부") boolean base64_encoded,
            @P("응답에 포함할 필드 목록 (쉼표로 구분)") String fields) {
        try {
            Pageable pageable = PageRequest.of(
                    Math.max(0, page - 1),
                    size > 0 ? size : 20,
                    Sort.by(Sort.Direction.DESC, "createdAt")
            );

            Page<Submission> submissionPage = submissionService.findAll(pageable);

            List<SubmissionResponse> submissions = submissionPage.getContent().stream()
                    .map(submission -> SubmissionResponse.from(submission, base64_encoded, parseFields(fields)))
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("submissions", submissions);
            response.put("meta", createPaginationMeta(submissionPage));
            return submissionPage;
        } catch (Exception e) {
            return null;
        }
    }

    // ========================================
    // 코드 채점 관련 도구
    // ========================================

    @Tool("채점 토큰으로 코드 채점 결과를 조회합니다. 테스트 케이스 통과 여부, 점수, 실패한 케이스 정보, 피드백 등을 확인할 수 있습니다.")
    public Grading getGradingByToken(@P("조회할 채점의 토큰") String gradingToken) {
        log.debug("🔧 [도구 호출] getGrading - 파라미터: gradingToken={}", gradingToken);
        Grading result = gradingService.findByToken(gradingToken);
        log.debug("✅ [도구 결과] getGrading - 상태: {}", result != null ? result.getStatus() : "null");
        return result;
    }

    @Tool("코드 채점 목록을 페이징으로 조회합니다. 문제 ID로 필터링할 수 있으며, 테스트 케이스 통과 여부와 점수를 확인할 수 있습니다.")
    public Page<Grading> getGrading(
            @P("페이지 번호 (1부터 시작)") int page,
            @P("페이지 크기 (기본값: 20)") int size,
            @P("특정 문제의 채점만 조회 (선택 사항, 없으면 0 또는 음수)") Long problemId,
            @P("코드를 Base64로 인코딩하여 반환할지 여부") boolean base64_encoded,
            @P("응답에 포함할 필드 목록 (쉼표로 구분)") String fields) {
        try {
            Pageable pageable = PageRequest.of(
                    Math.max(0, page - 1),
                    size > 0 ? size : 20,
                    Sort.by(Sort.Direction.DESC, "createdAt")
            );

            Page<Grading> gradingPage;
            if (problemId != null && problemId > 0) {
                gradingPage = gradingService.findByProblemId(problemId, pageable);
            } else {
                gradingPage = gradingService.findAll(pageable);
            }

            List<GradingResponse> gradings = gradingPage.getContent().stream()
                    .map(grading -> GradingResponse.from(grading, base64_encoded, parseFields(fields)))
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("grading", gradings);
            response.put("meta", createPaginationMeta(gradingPage));

            return gradingPage;
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    // ========================================
    // 헬퍼 메서드
    // ========================================

    private String[] parseFields(String fields) {
        if (fields == null || fields.trim().isEmpty()) {
            return null;
        }
        return fields.split(",");
    }

    private <T> Map<String, Object> createPaginationMeta(Page<T> page) {
        Map<String, Object> meta = new HashMap<>();
        meta.put("current_page", page.getNumber() + 1);
        meta.put("next_page", page.hasNext() ? page.getNumber() + 2 : null);
        meta.put("prev_page", page.hasPrevious() ? page.getNumber() : null);
        meta.put("total_pages", page.getTotalPages());
        meta.put("total_count", page.getTotalElements());
        meta.put("per_page", page.getSize());
        return meta;
    }
}
