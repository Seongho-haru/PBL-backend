package com.PBL.lab.core.dto;

import com.PBL.lab.core.entity.Language;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 코드 실행 요청 데이터 클래스
 *
 * Docker 컨테이너에서 코드를 실행하기 위한 모든 정보를 포함하는 DTO입니다.
 * DockerExecutionService에서 실제 코드 실행 시 사용됩니다.
 *
 * 포함 정보:
 * - sourceCode: 실행할 소스코드 (단일 파일)
 * - language: 프로그래밍 언어 정보 (컴파일러, 실행 명령어 등)
 * - stdin: 표준 입력 데이터
 * - expectedOutput: 예상 출력 (채점용)
 * - additionalFiles: 추가 파일들 (프로젝트 제출 시)
 * - constraints: 보안 제약조건 (시간, 메모리, 프로세스 제한 등)
 * - compilerOptions: 컴파일러 옵션
 * - commandLineArguments: 실행 시 명령행 인자
 * - redirectStderrToStdout: 에러 출력을 표준 출력으로 리다이렉션 여부
 * - enableNetwork: 네트워크 접근 허용 여부
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CodeExecutionRequest {
    private String sourceCode;              // 소스 코드
    private Language language;              // 프로그래밍 언어
    private String stdin;                   // 표준 입력
    private String expectedOutput;          // 예상 출력
    private SecurityConstraints constraints; // 보안 제약조건
    private byte[] additionalFiles;         // 추가 파일 (바이너리)
    private String compilerOptions;         // 컴파일러 옵션
    private String commandLineArguments;    // 명령행 인자
    private Boolean redirectStderrToStdout; // stderr → stdout 리다이렉션
    private Boolean enableNetwork;          // 네트워크 허용
}