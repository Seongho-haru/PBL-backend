package com.PBL.lab.judge0.dto;

import com.PBL.lab.judge0.entity.SubmissionInputOutput;
import lombok.Builder;
import lombok.Data;

/**
 * Input/Output DTO - 입출력 데이터 전송 객체
 * 
 * === 주요 목적 ===
 * - 코드 실행 시 필요한 입출력 데이터를 전송하기 위한 DTO
 * - 클라이언트와 서버 간 입출력 정보 교환
 * - 테스트 케이스 실행 결과 전달
 * 
 * === 포함 데이터 ===
 * - stdin: 프로그램 실행 시 표준 입력 데이터
 * - expectedOutput: 예상 출력 결과 (정답 비교용)
 * - stdout: 프로그램의 표준 출력 결과
 * - stderr: 프로그램의 표준 오류 출력
 * - compileOutput: 컴파일 과정에서의 출력 및 오류 메시지
 * - message: 추가적인 실행 관련 메시지
 */
@Data
@Builder
public class InputOutput {

    //채점시 에만 사용
    private String token;

    /**
     * 표준 입력 데이터
     * - 프로그램 실행 시 stdin으로 전달될 입력 데이터
     * - 테스트 케이스의 입력값
     */
    private String stdin;

    /**
     * 예상 출력 결과
     * - 정답 비교를 위한 기대 출력값
     * - stdout과 비교하여 정답 여부 판단
     */
    private String expectedOutput;

    /**
     * 프로그램의 표준 출력 결과
     * - 실행된 프로그램이 stdout으로 출력한 내용
     * - expectedOutput과 비교하여 정답 여부 판단
     */
    private String stdout;

    /**
     * 프로그램의 표준 오류 출력
     * - 실행된 프로그램이 stderr로 출력한 오류 메시지
     * - 컴파일 오류, 런타임 오류 등
     */
    private String stderr;

    /**
     * 컴파일 출력 및 오류 메시지
     * - 컴파일 과정에서 발생한 모든 출력
     * - 컴파일 경고, 오류 메시지, 링크 정보 등
     */
    private String compileOutput;

    /**
     * 추가적인 실행 관련 메시지
     * - 시스템에서 생성한 추가 정보나 설명
     * - 오류 발생 시 상세한 원인 설명
     * - 디버깅을 위한 추가 컨텍스트 정보
     */
    private String message;

    /**
     * SubmissionInputOutput 엔티티로부터 InputOutput DTO를 생성하는 정적 팩토리 메서드
     * 
     * @param entity 변환할 SubmissionInputOutput 엔티티
     * @return InputOutput DTO 객체
     */
    public static InputOutput from(SubmissionInputOutput entity) {
        if (entity == null) {
            return null;
        }
        
        return InputOutput.builder()
                .stdin(entity.getStdin())
                .expectedOutput(entity.getExpectedOutput())
                .stdout(entity.getStdout())
                .stderr(entity.getStderr())
                .compileOutput(entity.getCompileOutput())
                .message(entity.getMessage())
                .build();
    }

    public static InputOutput from(String token, SubmissionInputOutput entity) {
        if (entity == null) {
            return null;
        }
        return InputOutput.builder()
                .token(token)
                .stdin(entity.getStdin())
                .expectedOutput(entity.getExpectedOutput())
                .stdout(entity.getStdout())
                .stderr(entity.getStderr())
                .compileOutput(entity.getCompileOutput())
                .message(entity.getMessage())
                .build();
    }

}
