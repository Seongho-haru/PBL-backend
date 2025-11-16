package com.PBL.ai.service;

import com.PBL.ai.config.Assisdent;
import com.PBL.ai.config.RAGconfig;
import com.PBL.ai.dto.GradingRequest;
import com.PBL.ai.tools.*;
import com.PBL.lab.grade.entity.Grade;
import com.PBL.lab.grade.service.GradeService;
import com.PBL.lecture.LectureService;
import com.PBL.lecture.entity.Lecture;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class AssistantService {
    @Autowired
    private final StreamingChatModel streamingChatModel; // 스트리밍용 모델

    private final BookTools bookTools;
    private final SubmissionTools submissionTools;
    private final LectureTools lectureTools;
    private final CurriculumTools curriculumTools;
    private final CommunityTools communityTools;
    private final RAGconfig ragconfig;
    private final GradeService gradeService;
    private final LectureService lectureService;
    private Assisdent assisdent;

    @PostConstruct
    public void init(){
        this.assisdent = AiServices.builder(Assisdent.class)
                .streamingChatModel(streamingChatModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
//                .contentRetriever(ragconfig.lectureContentRetriever()) 
                .tools(bookTools, submissionTools, lectureTools, curriculumTools, communityTools)
                .build();
    }

    /**
     * 스트리밍 방식으로 분석 및 해설 제공
     * @param submission 제출 정보
     * @param problem 문제 정보
     * @return Flux<String> 스트림
     */
    @Transactional(readOnly = true)
    public Flux<String> analyAndExplainStream(GradingRequest request) {
        // 1. 제출한 토큰을 활용해서 채점 정보 가져오기
        Grade grade = gradeService.findByToken(request.getGradingToken());

        // 2. 문제 정보 가져오기
        Lecture lecture = lectureService.getLecture(request.getProblemId())
                .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다: " + request.getProblemId()));

        return assisdent.gradingStream(grade.toString(), lecture.toString());
    }



}
