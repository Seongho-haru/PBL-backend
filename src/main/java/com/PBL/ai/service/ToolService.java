package com.PBL.ai.service;

import com.PBL.lab.grading.entity.Grading;
import com.PBL.lab.grading.service.GradingService;
import com.PBL.lab.judge0.entity.Submission;
import com.PBL.lab.judge0.service.SubmissionService;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ToolService {
    private final SubmissionService submissionService;
    private final GradingService gradingService;

    //1. 코드 실행 조회
    @Tool("제출 토큰으로 코드 실행 결과를 조회합니다. 실행 상태, 출력 결과, 에러 메시지 등을 확인할 수 있습니다.")
    public Submission getSubmission (@P("조회할 제출의 토큰")String submissionToken){
        return submissionService.findByToken(submissionToken);
    }

    //2. 코드 체점 제출 조회
    @Tool("채점 토큰으로 코드 채점 결과를 조회합니다. 테스트 케이스 통과 여부, 점수, 피드백 등을 확인할 수 있습니다.")
    public Grading getGrading(@P("조회할 채점의 토큰")String gradingToken){
        return gradingService.findByToken(gradingToken);
    }

    //3. 강의 조회

    //4. 커리큘럼조회

    //5. RAG조회

    /*
     * TODO 추후 강의 및 커리커률럼 자동 생성 및 조힙을 위한 도구
     *  1. 강의 생성
     *  2. 커리큘럼 생성
     *  3. 정보 보강을 위한 인터넷 검색
     */


}
