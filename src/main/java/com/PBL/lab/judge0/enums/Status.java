package com.PBL.lab.judge0.enums;

import lombok.Getter;

/**
 * Status Enumeration - 제출 실행 상태 열거형
 * 
 * 목적:
 * - 코드 제출의 실행 상태를 나타내는 표준 상태 코드 체계
 * - 원본 Judge0 Ruby Status 열거형의 완전한 Java 포팅
 * - 온라인 저지 시스템의 국제 표준 상태 코드와 호환
 * 
 * 상태 코드 체계:
 * - 1-2: 진행 상태 (In Queue, Processing)
 * - 3-5: 실행 결과 상태 (Accepted, Wrong Answer, Time Limit Exceeded)
 * - 6-7: 컴파일 관련 오류 (Memory Limit Exceeded, Compilation Error)
 * - 8-13: 런타임 오류 (다양한 시그널 기반 분류)
 * - 14-15: 내부 시스템 오류 (Internal Error, Exec Format Error)
 * 
 * 주요 상태 설명:
 * - QUEUE(1): 대기열에서 실행을 기다리는 상태
 * - PROCESS(2): 현재 실행 중인 상태
 * - AC(3): Accepted - 정답, 모든 테스트 케이스 통과
 * - WA(4): Wrong Answer - 오답, 예상 출력과 다른 결과
 * - TLE(5): Time Limit Exceeded - 시간 초과
 * - MLE(6): Memory Limit Exceeded - 메모리 초과
 * - CE(7): Compilation Error - 컴파일 오류
 * 
 * 런타임 오류 분류:
 * - SIGSEGV(8): Segmentation Fault - 메모리 접근 오류
 * - SIGXFSZ(9): File Size Limit Exceeded - 파일 크기 초과
 * - SIGFPE(10): Floating Point Exception - 부동소수점 연산 오류
 * - SIGABRT(11): Program Aborted - 프로그램 강제 종료
 * - NZEC(12): Non-Zero Exit Code - 비정상 종료 코드
 * - OTHER(13): Other Runtime Error - 기타 런타임 오류
 * 
 * 시스템 오류:
 * - BOXERR(14): Internal Error - 샌드박스 또는 시스템 내부 오류
 * - EXEERR(15): Exec Format Error - 실행 파일 형식 오류
 * 
 * 상태 분류 메서드:
 * - isTerminal(): 최종 상태 여부 (QUEUE, PROCESS 제외한 모든 상태)
 * - isError(): 오류 상태 여부 (MLE 이상의 모든 상태)
 * - isRuntimeError(): 런타임 오류 여부 (SIGSEGV ~ OTHER)
 * - isSuccessful(): 성공 상태 여부 (AC만)
 * 
 * 신호 코드 매핑:
 * - Unix/Linux 시그널 코드를 Judge0 상태 코드로 변환
 * - findRuntimeErrorByStatusCode(): 시그널 번호 기반 상태 결정
 * - 표준 POSIX 시그널과의 호환성 보장
 * 
 * 국제 표준 호환성:
 * - ACM-ICPC, IOI 등 국제 프로그래밍 대회 표준과 일치
 * - Codeforces, AtCoder 등 주요 온라인 저지와 호환
 * - 다양한 프로그래밍 언어와 플랫폼에서 일관된 상태 보고
 */
@Getter
public enum Status {
    
    QUEUE(1, "In Queue"),
    PROCESS(2, "Processing"),
    AC(3, "Accepted"),
    WA(4, "Wrong Answer"),
    TLE(5, "Time Limit Exceeded"),
    MLE(6, "Memory Limit Exceeded"),
    CE(7, "Compilation Error"),
    SIGSEGV(8, "Runtime Error (SIGSEGV)"),
    SIGXFSZ(9, "Runtime Error (SIGXFSZ)"),
    SIGFPE(10, "Runtime Error (SIGFPE)"),
    SIGABRT(11, "Runtime Error (SIGABRT)"),
    NZEC(12, "Runtime Error (NZEC)"),
    OTHER(13, "Runtime Error (Other)"),
    BOXERR(14, "Internal Error"),
    EXEERR(15, "Exec Format Error");

    private final int id;
    private final String name;

    Status(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Get Status by ID
     */
    public static Status fromId(Integer id) {
        if (id == null) {
            return null;
        }
        
        for (Status status : Status.values()) {
            if (status.id == id) {
                return status;
            }
        }
        
        throw new IllegalArgumentException("Unknown status ID: " + id);
    }

    /**
     * Get Status by name
     */
    public static Status fromName(String name) {
        if (name == null) {
            return null;
        }
        
        for (Status status : Status.values()) {
            if (status.name.equals(name)) {
                return status;
            }
        }
        
        throw new IllegalArgumentException("Unknown status name: " + name);
    }

    /**
     * Find runtime error status by signal code
     */
    public static Status findRuntimeErrorByStatusCode(Integer statusCode) {
        if (statusCode == null) {
            return OTHER;
        }
        
        return switch (statusCode) {
            case 11 -> SIGSEGV;  // SIGSEGV
            case 25 -> SIGXFSZ;  // SIGXFSZ
            case 8 -> SIGFPE;    // SIGFPE
            case 6 -> SIGABRT;   // SIGABRT
            default -> OTHER;
        };
    }

    /**
     * Check if this status represents a terminal state
     */
    public boolean isTerminal() {
        return this != QUEUE && this != PROCESS;
    }

    /**
     * Check if this status represents an error
     */
    public boolean isError() {
        return id >= 6; // MLE and above are errors
    }

    /**
     * Check if this status represents a runtime error
     */
    public boolean isRuntimeError() {
        return id >= 8 && id <= 13;
    }

    /**
     * Check if this status represents successful execution
     */
    public boolean isSuccessful() {
        return this == AC;
    }

    @Override
    public String toString() {
        return name;
    }
}
