package com.PBL.ai;

import com.PBL.ai.service.AssistantService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * AI Tool 호출 JUnit 테스트
 * AI가 실제로 도구를 호출하는지 확인하기 위한 테스트
 * 
 * 실행 방법:
 * 1. IDE에서 직접 실행 (Run Test)
 * 2. 터미널: ./gradlew test --tests AiToolTest
 */
@SpringBootTest
public class AiToolTest {

    @Autowired
    private AssistantService assistantService;

    @Test
    @DisplayName("코드 해설 - 도구 호출 테스트")
    public void testCodeExplanation() {
        String testSubmission = """
            {
                "token": "test-token-001",
                "sourceCode": "def sum(a, b):\\n    return a + b",
                "status": "Wrong Answer"
            }
            """;

        String testProblem = """
            {
                "id": 1,
                "title": "두 수의 합",
                "description": "두 정수 a와 b를 입력받아 합을 출력하는 프로그램을 작성하세요."
            }
            """;

        assistantService.analyAndExplainStream(testSubmission, testProblem)
                .doOnNext(System.out::print)
                .blockLast();
        
        System.out.println("\n--- 테스트 완료 ---");
    }

    @Test
    @DisplayName("강의 추천 - 검색 도구 호출 테스트")
    public void testLectureRecommendation() {
        String userRequest = "알고리즘 공부를 시작하고 싶습니다. 초보자에게 적합한 강의를 추천해주세요.";
        String context = "사용자는 프로그래밍 기초는 알고 있지만 알고리즘은 처음입니다.";

        assistantService.analyAndExplainStream(userRequest, context)
                .doOnNext(System.out::print)
                .blockLast();
        
        System.out.println("\n--- 테스트 완료 ---");
    }

    @Test
    @DisplayName("커리큘럼 추천 - 복합 도구 호출 테스트")
    public void testCurriculumRecommendation() {
        String userRequest = "Python을 체계적으로 학습할 수 있는 커리큘럼을 추천해주세요.";
        String context = "완전 초보자입니다.";

        assistantService.analyAndExplainStream(userRequest, context)
                .doOnNext(System.out::print)
                .blockLast();
        
        System.out.println("\n--- 테스트 완료 ---");
    }
}

