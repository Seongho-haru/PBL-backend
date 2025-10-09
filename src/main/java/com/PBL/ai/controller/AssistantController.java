package com.PBL.ai.controller;

import com.PBL.ai.dto.GradingRequest;
import com.PBL.ai.service.AssistantService;
import com.PBL.lab.grading.entity.Grading;
import com.PBL.lab.grading.service.GradingService;
import com.PBL.lecture.Lecture;
import com.PBL.lecture.LectureService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@Slf4j
class AssistantController {
    private final GradingService gradingService;
    private final LectureService lectureService;
    private final AssistantService assistantService;

    //1. 코드 실행/제출 에대한 해설
    //2. 커리큘럼 생성 또는 조합

    /**
     * 코딩테스트 해설 보기 (스트리밍 방식)
     * @param request 제출 토큰 및 문제 ID
     * @return Flux<String> - 실시간 스트리밍 응답
     */
    @PostMapping(value = "/chat/grading", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> gradingStream(@RequestBody GradingRequest request) {
        // 1. 제출한 토큰을 활용해서 채점 정보 가져오기
        Grading grading = gradingService.findByToken(request.getGradingToken());

        // 2. 문제 정보 가져오기
        Lecture lecture = lectureService.getLecture(request.getProblemId())
                .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다: " + request.getProblemId()));

        // 3. AI 분석 및 해설 생성 (스트리밍) - Spring이 자동으로 SSE 처리!
        return assistantService.analyAndExplainStream(grading, lecture)
                .doOnNext(token -> log.debug("토큰 전송: {}", token))
                .doOnComplete(() -> log.info("스트리밍 완료"))
                .doOnError(error -> log.error("스트리밍 에러", error));
    }


}
