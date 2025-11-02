package com.PBL.lab.core.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Execution Limits Configuration
 *
 * Judge0 시스템의 실행 제한 설정을 관리합니다.
 * 기본값, 최대값, 제어 플래그를 포함한 종합적인 리소스 제한 관리를 제공합니다.
 *
 * 주요 기능:
 * - CPU 시간 제한 설정 (기본값 및 최대값)
 * - 메모리 사용 제한 설정 (기본값 및 최대값)
 * - 프로세스/스레드 제한 설정
 * - 파일 크기 제한 설정
 * - 다중 실행 횟수 설정
 * - 프로세스별 제한 활성화 제어
 * - 제한값 유효성 검증
 *
 * 사용 시나리오:
 * - 시스템 리소스 보호
 * - 무한 루프 방지
 * - 메모리 초과 방지
 * - 공정한 리소스 분배
 */
@Component
@ConfigurationProperties(prefix = "judge0.execution")
@Getter
@Setter
public class ExecutionLimitsConfig {

    // ========== 기본값 (Defaults) ==========

    /**
     * 다중 실행 횟수 기본값
     * - 동일한 코드를 여러 번 실행하여 안정성 검증
     * - 기본값: 1회
     * - 성능 측정의 정확도를 높이기 위해 사용
     * - 최대값은 maxNumberOfRuns로 제한됨
     */
    private Integer numberOfRuns = 1;

    /**
     * CPU 실행 시간 제한 기본값 (초)
     * - 프로그램이 CPU를 사용할 수 있는 최대 시간
     * - 기본값: 5.0초
     * - 이 시간을 초과하면 Time Limit Exceeded 오류 발생
     * - 최대값은 maxCpuTimeLimit으로 제한됨
     */
    private BigDecimal cpuTimeLimit = new BigDecimal("5.0");

    /**
     * CPU 추가 여유 시간 기본값 (초)
     * - 컴파일, 링크 등 추가 작업을 위한 여유 시간
     * - 기본값: 1.0초
     * - cpuTimeLimit에 추가로 제공되는 시간
     * - 최대값은 maxCpuExtraTime으로 제한됨
     */
    private BigDecimal cpuExtraTime = new BigDecimal("1.0");

    /**
     * 전체 실행 제한 시간 기본값 (초)
     * - 프로그램 실행의 절대적 시간 제한
     * - 기본값: 10.0초
     * - 이 시간을 초과하면 강제 종료
     * - 최대값은 maxWallTimeLimit으로 제한됨
     */
    private BigDecimal wallTimeLimit = new BigDecimal("10.0");

    /**
     * 메모리 사용 제한 기본값 (KB 단위)
     * - 프로그램이 사용할 수 있는 최대 메모리 양
     * - 기본값: 128MB (128000KB)
     * - 이 메모리를 초과하면 Memory Limit Exceeded 오류 발생
     * - 최대값은 maxMemoryLimit으로 제한됨
     */
    private Integer memoryLimit = 128000;

    /**
     * 스택 메모리 제한 기본값 (KB 단위)
     * - 프로그램의 스택 영역 사용 제한
     * - 기본값: 64MB (64000KB)
     * - 재귀 호출이나 큰 지역 변수 사용 시 중요
     * - 최대값은 maxStackLimit으로 제한됨
     */
    private Integer stackLimit = 64000;

    /**
     * 최대 프로세스/스레드 수 기본값
     * - 프로그램이 생성할 수 있는 최대 프로세스 및 스레드 수
     * - 기본값: 60개
     * - 멀티프로세싱, 멀티스레딩 프로그램의 리소스 제한
     * - 최대값은 maxMaxProcessesAndOrThreads로 제한됨
     */
    private Integer maxProcessesAndOrThreads = 60;

    /**
     * 최대 파일 크기 제한 기본값 (KB 단위)
     * - 프로그램이 생성할 수 있는 최대 파일 크기
     * - 기본값: 1MB (1024KB)
     * - 파일 출력이 많은 프로그램의 리소스 제한
     * - 최대값은 maxMaxFileSize로 제한됨
     */
    private Integer maxFileSize = 1024;

    /**
     * 프로세스/스레드별 시간 제한 활성화 기본값
     * - 각 프로세스/스레드에 개별적으로 시간 제한 적용
     * - 기본값: false (비활성화)
     * - 멀티프로세싱 환경에서 개별 프로세스 제어
     */
    private Boolean enablePerProcessAndThreadTimeLimit = false;

    /**
     * 프로세스/스레드별 메모리 제한 활성화 기본값
     * - 각 프로세스/스레드에 개별적으로 메모리 제한 적용
     * - 기본값: false (비활성화)
     * - 멀티프로세싱 환경에서 개별 프로세스 메모리 제어
     */
    private Boolean enablePerProcessAndThreadMemoryLimit = false;

    /**
     * 표준 오류를 표준 출력으로 리다이렉션 기본값
     * - stderr를 stdout으로 합쳐서 출력
     * - 기본값: false (분리 유지)
     * - 디버깅이나 로그 통합 시 유용
     */
    private Boolean redirectStderrToStdout = false;

    // ========== 최대값 (Maximums) ==========

    /**
     * 다중 실행 횟수 최대값
     * - 사용자가 설정할 수 있는 최대 실행 횟수
     * - 기본값: 20회
     * - 시스템 리소스 보호를 위한 상한선
     * - 이 값을 초과하면 IllegalArgumentException 발생
     */
    private Integer maxNumberOfRuns = 20;

    /**
     * CPU 실행 시간 제한 최대값 (초)
     * - 사용자가 설정할 수 있는 최대 CPU 시간
     * - 기본값: 15.0초
     * - 시스템 리소스 보호를 위한 상한선
     * - 이 값을 초과하면 IllegalArgumentException 발생
     */
    private BigDecimal maxCpuTimeLimit = new BigDecimal("15.0");

    /**
     * CPU 추가 여유 시간 최대값 (초)
     * - 사용자가 설정할 수 있는 최대 추가 시간
     * - 기본값: 5.0초
     * - 시스템 리소스 보호를 위한 상한선
     * - 이 값을 초과하면 IllegalArgumentException 발생
     */
    private BigDecimal maxCpuExtraTime = new BigDecimal("5.0");

    /**
     * 전체 실행 제한 시간 최대값 (초)
     * - 사용자가 설정할 수 있는 최대 전체 실행 시간
     * - 기본값: 20.0초
     * - 시스템 리소스 보호를 위한 상한선
     * - 이 값을 초과하면 IllegalArgumentException 발생
     */
    private BigDecimal maxWallTimeLimit = new BigDecimal("20.0");

    /**
     * 메모리 사용 제한 최대값 (KB 단위)
     * - 사용자가 설정할 수 있는 최대 메모리 사용량
     * - 기본값: 512MB (512000KB)
     * - 시스템 리소스 보호를 위한 상한선
     * - 이 값을 초과하면 IllegalArgumentException 발생
     */
    private Integer maxMemoryLimit = 512000;

    /**
     * 스택 메모리 제한 최대값 (KB 단위)
     * - 사용자가 설정할 수 있는 최대 스택 메모리 사용량
     * - 기본값: 128MB (128000KB)
     * - 시스템 리소스 보호를 위한 상한선
     * - 이 값을 초과하면 IllegalArgumentException 발생
     */
    private Integer maxStackLimit = 128000;

    /**
     * 최대 프로세스/스레드 수 최대값
     * - 사용자가 설정할 수 있는 최대 프로세스/스레드 수
     * - 기본값: 120개
     * - 시스템 리소스 보호를 위한 상한선
     * - 이 값을 초과하면 IllegalArgumentException 발생
     */
    private Integer maxMaxProcessesAndOrThreads = 120;

    /**
     * 최대 파일 크기 제한 최대값 (KB 단위)
     * - 사용자가 설정할 수 있는 최대 파일 크기
     * - 기본값: 4MB (4096KB)
     * - 시스템 리소스 보호를 위한 상한선
     * - 이 값을 초과하면 IllegalArgumentException 발생
     */
    private Integer maxMaxFileSize = 4096;

    /**
     * 최대 압축 해제 크기 (KB 단위)
     * - 추가 파일 압축 해제 시 최대 허용 크기
     * - 기본값: 10MB (10240KB)
     * - 악의적인 압축 파일로 인한 시스템 공격 방지
     * - 이 값을 초과하면 압축 해제 실패
     */
    private Integer maxExtractSize = 10240;

    // ========== 프로세스별 제한 제어 (Per-process Limits Control) ==========

    /**
     * 프로세스/스레드별 시간 제한 허용 여부
     * - true: 사용자가 프로세스/스레드별 시간 제한을 활성화할 수 있음 (기본값)
     * - false: 프로세스/스레드별 시간 제한 설정 비활성화
     * - 멀티프로세싱 환경에서 세밀한 제어가 필요할 때 사용
     */
    private Boolean allowEnablePerProcessAndThreadTimeLimit = true;

    /**
     * 프로세스/스레드별 메모리 제한 허용 여부
     * - true: 사용자가 프로세스/스레드별 메모리 제한을 활성화할 수 있음 (기본값)
     * - false: 프로세스/스레드별 메모리 제한 설정 비활성화
     * - 멀티프로세싱 환경에서 세밀한 제어가 필요할 때 사용
     */
    private Boolean allowEnablePerProcessAndThreadMemoryLimit = true;

    // ========== 유틸리티 메서드 (Utility Methods) ==========

    /**
     * 실행 제한값 유효성 검증
     * - 사용자가 설정한 실행 제한값들이 시스템 최대값을 초과하지 않는지 검증
     * - 각 제한값이 유효한 범위 내에 있는지 확인
     * - 유효하지 않은 값이 있으면 IllegalArgumentException 발생
     * - SecurityManager에서 비슷한 검증 수행
     *
     * @param numberOfRuns 다중 실행 횟수 (1 ~ maxNumberOfRuns)
     * @param cpuTimeLimit CPU 실행 시간 제한 (0 ~ maxCpuTimeLimit 초)
     * @param cpuExtraTime CPU 추가 여유 시간 (0 ~ maxCpuExtraTime 초)
     * @param wallTimeLimit 전체 실행 제한 시간 (1 ~ maxWallTimeLimit 초)
     * @param memoryLimit 메모리 사용 제한 (2048 ~ maxMemoryLimit KB)
     * @param stackLimit 스택 메모리 제한 (0 ~ maxStackLimit KB)
     * @param maxProcesses 최대 프로세스/스레드 수 (1 ~ maxMaxProcessesAndOrThreads)
     * @param maxFileSize 최대 파일 크기 (0 ~ maxMaxFileSize KB)
     * @throws IllegalArgumentException 제한값이 유효 범위를 벗어난 경우
     */
    public void validateLimits(Integer numberOfRuns, BigDecimal cpuTimeLimit, BigDecimal cpuExtraTime,
                              BigDecimal wallTimeLimit, Integer memoryLimit, Integer stackLimit,
                              Integer maxProcesses, Integer maxFileSize) {

        // 다중 실행 횟수 검증: 1회 이상, 최대값 이하
        if (numberOfRuns != null && (numberOfRuns < 1 || numberOfRuns > this.maxNumberOfRuns)) {
            throw new IllegalArgumentException("Number of runs must be between 1 and " + this.maxNumberOfRuns);
        }

        // CPU 실행 시간 제한 검증: 0초 이상, 최대값 이하
        if (cpuTimeLimit != null && (cpuTimeLimit.compareTo(BigDecimal.ZERO) < 0 || cpuTimeLimit.compareTo(this.maxCpuTimeLimit) > 0)) {
            throw new IllegalArgumentException("CPU time limit must be between 0 and " + this.maxCpuTimeLimit + " seconds");
        }

        // CPU 추가 여유 시간 검증: 0초 이상, 최대값 이하
        if (cpuExtraTime != null && (cpuExtraTime.compareTo(BigDecimal.ZERO) < 0 || cpuExtraTime.compareTo(this.maxCpuExtraTime) > 0)) {
            throw new IllegalArgumentException("CPU extra time must be between 0 and " + this.maxCpuExtraTime + " seconds");
        }

        // 전체 실행 제한 시간 검증: 1초 이상, 최대값 이하
        if (wallTimeLimit != null && (wallTimeLimit.compareTo(BigDecimal.ONE) < 0 || wallTimeLimit.compareTo(this.maxWallTimeLimit) > 0)) {
            throw new IllegalArgumentException("Wall time limit must be between 1 and " + this.maxWallTimeLimit + " seconds");
        }

        // 메모리 사용 제한 검증: 2MB 이상, 최대값 이하
        if (memoryLimit != null && (memoryLimit < 2048 || memoryLimit > this.maxMemoryLimit)) {
            throw new IllegalArgumentException("Memory limit must be between 2048 and " + this.maxMemoryLimit + " KB");
        }

        // 스택 메모리 제한 검증: 0KB 이상, 최대값 이하
        if (stackLimit != null && (stackLimit < 0 || stackLimit > this.maxStackLimit)) {
            throw new IllegalArgumentException("Stack limit must be between 0 and " + this.maxStackLimit + " KB");
        }

        // 최대 프로세스/스레드 수 검증: 1개 이상, 최대값 이하
        if (maxProcesses != null && (maxProcesses < 1 || maxProcesses > this.maxMaxProcessesAndOrThreads)) {
            throw new IllegalArgumentException("Max processes must be between 1 and " + this.maxMaxProcessesAndOrThreads);
        }

        // 최대 파일 크기 검증: 0KB 이상, 최대값 이하
        if (maxFileSize != null && (maxFileSize < 0 || maxFileSize > this.maxMaxFileSize)) {
            throw new IllegalArgumentException("Max file size must be between 0 and " + this.maxMaxFileSize + " KB");
        }
    }
}
