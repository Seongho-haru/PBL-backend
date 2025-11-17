package com.PBL.lab.core.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;

/**
 * Execution Input/Output Entity - 코드 실행 입출력 정보 엔티티
 *
 * === 주요 목적 ===
 * - 코드 실행 시 필요한 입출력 데이터를 관리하는 엔티티
 * - 표준 입력, 예상 출력, 실제 출력 결과 등을 저장
 * - 대용량 텍스트 데이터를 효율적으로 관리
 * - Submission과 Grading 모두에서 공통으로 사용
 *
 * === 포함 데이터 ===
 * - stdin: 프로그램 실행 시 표준 입력 데이터
 * - expectedOutput: 예상 출력 결과 (정답 비교용)
 * - stdout: 프로그램의 표준 출력 결과
 * - stderr: 프로그램의 표준 오류 출력
 * - compileOutput: 컴파일 과정에서의 출력 및 오류 메시지
 * - message: 추가적인 실행 관련 메시지
 */
@Entity
@Table(name = "input_output")
@Data
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExecutionInputOutput {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 표준 입력 데이터
     * - 프로그램 실행 시 stdin으로 전달될 입력 데이터
     * - 테스트 케이스의 입력값 저장
     * - TEXT 타입으로 대용량 입력 지원
     */
    @Lob
    @Column(columnDefinition = "TEXT")
    private String stdin;

    /**
     * 예상 출력 결과
     * - 정답 비교를 위한 기대 출력값
     * - stdout과 비교하여 정답 여부 판단
     * - TEXT 타입으로 대용량 출력 지원
     */
    @Lob
    @Column(name = "expected_output", columnDefinition = "TEXT")
    private String expectedOutput;

    /**
     * 프로그램의 표준 출력 결과
     * - 실행된 프로그램이 stdout으로 출력한 내용
     * - expectedOutput과 비교하여 정답 여부 판단
     * - TEXT 타입으로 대용량 출력 지원
     */
    @Lob
    @Column(columnDefinition = "TEXT")
    private String stdout;

    /**
     * 프로그램의 표준 오류 출력
     * - 실행된 프로그램이 stderr로 출력한 오류 메시지
     * - 컴파일 오류, 런타임 오류 등 저장
     * - TEXT 타입으로 대용량 오류 메시지 지원
     */
    @Lob
    @Column(columnDefinition = "TEXT")
    private String stderr;

    /**
     * 컴파일 출력 및 오류 메시지
     * - 컴파일 과정에서 발생한 모든 출력
     * - 컴파일 경고, 오류 메시지, 링크 정보 등
     * - TEXT 타입으로 대용량 컴파일 로그 지원
     */
    @Lob
    @Column(name = "compile_output", columnDefinition = "TEXT")
    private String compileOutput;

    /**
     * 추가적인 실행 관련 메시지
     * - 시스템에서 생성한 추가 정보나 설명
     * - 오류 발생 시 상세한 원인 설명
     * - 디버깅을 위한 추가 컨텍스트 정보
     */
    @Lob
    @Column(columnDefinition = "TEXT")
    private String message;

}
