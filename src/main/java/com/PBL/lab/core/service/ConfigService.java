package com.PBL.lab.core.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Configuration Service - Judge0 시스템 설정 관리 서비스
 * 
 * 목적:
 * - Judge0 시스템의 모든 설정 매개변수 중앙 집중 관리
 * - 원본 Judge0 Ruby Config 헬퍼와 동등한 역할
 * - 실시간 설정 변경 및 시스템 동작 제어
 * 
 * 주요 설정 영역:
 * 1. 시스템 운영: 유지보수 모드, 대기열 크기 제한
 * 2. 기능 플래그: 동기 실행, 컴파일러 옵션, 콜백 등 기능 온/오프
 * 3. 실행 제한: CPU, 메모리, 시간 제한 기본값 및 최대값
 * 4. 보안 설정: 허용 언어, 시스템 호출, 새드박스 설정
 * 5. Docker 설정: 컨테이너 수, 제한 시간, 데몬 연결
 * 6. 외부 연동: 콜백 재시도, 제한 시간 등
 * 
 * 주요 기능:
 * - getConfigInfo(): 전체 설정 정보를 Map 형태로 반환
 * - validateLimits(): 실행 제한값 유효성 검증
 * - getAllowedLanguagesForCompilerOptions(): 컴파일러 옵션 허용 언어 목록
 * - getAllowedSyscalls(): 허용된 시스템 호출 목록
 * 
 * 설정 방식:
 * - @Value 어노테이션으로 application.yml에서 자동 로드
 * - 기본값 제공로 안전한 시스템 운영 보장
 * - 환경변수로 운영 환경별 오버라이드 가능
 * 
 * 보안 전략:
 * - 기본적으로 안전한 기본값 (보수적 설정)
 * - 고급 기능은 명시적 활성화 필요
 * - 시스템 리소스 보호를 위한 제한 설정
 */
@Service
@Getter
public class ConfigService {

    // ========== 시스템 운영 설정 (System Configuration) ==========
    
    /**
     * 유지보수 모드 활성화 여부
     * - true: 시스템이 유지보수 모드로 전환되어 새로운 제출을 받지 않음
     * - false: 정상 운영 모드 (기본값)
     * - 운영 중 서버 점검이나 업데이트 시 사용
     */
    @Value("${judge0.maintenance.mode:false}")
    private boolean maintenanceMode;

    /**
     * 유지보수 모드 시 표시할 메시지
     * - 유지보수 모드가 활성화되었을 때 클라이언트에게 보여줄 안내 메시지
     * - API 응답에 포함되어 사용자에게 상황을 알림
     */
    @Value("${judge0.maintenance.message:Judge0 is currently in maintenance.}")
    private String maintenanceMessage;

    // ========== 기능 플래그 (Feature Flags) ==========
    
    /**
     * 동기 실행 결과 대기 기능 활성화 여부
     * - true: wait=true 파라미터로 동기 실행 결과 대기 허용 (기본값)
     * - false: 동기 실행 비활성화, 모든 제출은 비동기로만 처리
     * - 대용량 트래픽 시 서버 부하 방지를 위해 비활성화 가능
     */
    @Value("${judge0.features.enable-wait-result:true}")
    private boolean enableWaitResult;

    /**
     * 컴파일러 옵션 설정 기능 활성화 여부
     * - true: 사용자가 컴파일러 옵션을 설정할 수 있음 (기본값)
     * - false: 컴파일러 옵션 설정 비활성화
     * - 보안상 위험한 컴파일러 옵션 사용을 방지하기 위해 비활성화 가능
     */
    @Value("${judge0.features.enable-compiler-options:true}")
    private boolean compilerOptionsEnabled;

    /**
     * 명령행 인수 설정 기능 활성화 여부
     * - true: 사용자가 프로그램 실행 시 명령행 인수를 설정할 수 있음 (기본값)
     * - false: 명령행 인수 설정 비활성화
     * - 보안상 위험한 명령행 인수 사용을 방지하기 위해 비활성화 가능
     */
    @Value("${judge0.features.enable-command-line-arguments:true}")
    private boolean commandLineArgumentsEnabled;

    /**
     * 제출 삭제 기능 활성화 여부
     * - true: 사용자가 제출한 코드를 삭제할 수 있음
     * - false: 제출 삭제 비활성화 (기본값, 보안상 안전)
     * - 데이터 보존을 위해 기본적으로 비활성화
     */
    @Value("${judge0.features.enable-submission-delete:false}")
    private boolean submissionDeleteEnabled;

    /**
     * 웹훅 콜백 기능 활성화 여부
     * - true: 실행 완료 시 외부 URL로 결과 전송 가능 (기본값)
     * - false: 웹훅 콜백 비활성화
     * - 외부 시스템과의 연동이 필요할 때 활성화
     */
    @Value("${judge0.features.enable-callbacks:true}")
    private boolean callbacksEnabled;

    /**
     * 추가 파일 업로드 기능 활성화 여부
     * - true: 프로젝트 제출 시 추가 파일 업로드 가능 (기본값)
     * - false: 추가 파일 업로드 비활성화
     * - 다중 파일 프로젝트 지원을 위해 필요
     */
    @Value("${judge0.features.enable-additional-files:true}")
    private boolean additionalFilesEnabled;

    /**
     * 배치 제출 기능 활성화 여부
     * - true: 여러 제출을 한 번에 처리하는 배치 API 사용 가능 (기본값)
     * - false: 배치 제출 비활성화
     * - 대량 제출 처리 시 성능 향상을 위해 사용
     */
    @Value("${judge0.features.enable-batched-submissions:true}")
    private boolean batchedSubmissionsEnabled;

    /**
     * 네트워크 접근 허용 설정 기능 활성화 여부
     * - true: 사용자가 네트워크 접근을 허용할 수 있음 (기본값)
     * - false: 네트워크 접근 설정 비활성화
     * - 보안상 네트워크 접근을 완전히 차단하고 싶을 때 사용
     */
    @Value("${judge0.features.allow-enable-network:true}")
    private boolean networkAllowed;

    /**
     * 네트워크 접근 기본 활성화 여부
     * - true: 기본적으로 네트워크 접근 허용
     * - false: 기본적으로 네트워크 접근 차단 (기본값, 보안상 안전)
     * - 외부 API 호출이 필요한 코드 실행 시에만 허용
     */
    @Value("${judge0.features.enable-network:false}")
    private boolean enableNetwork;

    /**
     * 네트워크 접근 설정 조회 메서드
     * - 현재 네트워크 접근이 허용되는지 여부를 반환
     * - 기본값과 사용자 설정을 고려한 최종 네트워크 접근 정책
     * 
     * @return true: 네트워크 접근 허용, false: 네트워크 접근 차단
     */
    public boolean getEnableNetwork() {
        return enableNetwork;
    }

    /**
     * 암시적 Base64 인코딩 비활성화 여부
     * - true: 자동 Base64 인코딩/디코딩 비활성화
     * - false: 자동 Base64 인코딩/디코딩 활성화 (기본값)
     * - 특수 문자나 바이너리 데이터 처리를 위해 사용
     */
    @Value("${judge0.features.disable-implicit-base64-encoding:false}")
    private boolean disableImplicitBase64Encoding;

    // ========== 대기열 설정 (Queue Configuration) ==========
    
    /**
     * 최대 대기열 크기
     * - 시스템이 동시에 처리할 수 있는 최대 제출 수
     * - 기본값: 100개
     * - 이 수를 초과하면 "queue is full" 오류 반환
     * - 서버 성능과 메모리 사용량에 따라 조정 필요
     */
    @Value("${judge0.queue.max-size:100}")
    private Integer maxQueueSize;

    /**
     * 최대 배치 제출 크기
     * - 한 번에 처리할 수 있는 최대 제출 수
     * - 기본값: 20개
     * - 배치 API 사용 시 한 번에 전송할 수 있는 제출 수 제한
     * - 서버 부하 방지와 메모리 사용량 제어를 위해 설정
     */
    @Value("${judge0.queue.max-submission-batch-size:20}")
    private Integer maxSubmissionBatchSize;

    // ========== 실행 제한 설정 - 기본값 (Execution Limits - Defaults) ==========
    
    /**
     * 다중 실행 횟수 기본값
     * - 동일한 코드를 여러 번 실행하여 안정성 검증
     * - 기본값: 1회
     * - 성능 측정의 정확도를 높이기 위해 사용
     * - 최대값은 maxNumberOfRuns로 제한됨
     */
    @Value("${judge0.execution.number-of-runs:1}")
    private Integer numberOfRuns;

    /**
     * CPU 실행 시간 제한 기본값 (초)
     * - 프로그램이 CPU를 사용할 수 있는 최대 시간
     * - 기본값: 5.0초
     * - 이 시간을 초과하면 Time Limit Exceeded 오류 발생
     * - 최대값은 maxCpuTimeLimit으로 제한됨
     */
    @Value("${judge0.execution.cpu-time-limit:5.0}")
    private BigDecimal cpuTimeLimit;

    /**
     * CPU 추가 여유 시간 기본값 (초)
     * - 컴파일, 링크 등 추가 작업을 위한 여유 시간
     * - 기본값: 1.0초
     * - cpuTimeLimit에 추가로 제공되는 시간
     * - 최대값은 maxCpuExtraTime으로 제한됨
     */
    @Value("${judge0.execution.cpu-extra-time:1.0}")
    private BigDecimal cpuExtraTime;

    /**
     * 전체 실행 제한 시간 기본값 (초)
     * - 프로그램 실행의 절대적 시간 제한
     * - 기본값: 10.0초
     * - 이 시간을 초과하면 강제 종료
     * - 최대값은 maxWallTimeLimit으로 제한됨
     */
    @Value("${judge0.execution.wall-time-limit:10.0}")
    private BigDecimal wallTimeLimit;

    /**
     * 메모리 사용 제한 기본값 (KB 단위)
     * - 프로그램이 사용할 수 있는 최대 메모리 양
     * - 기본값: 128MB (128000KB)
     * - 이 메모리를 초과하면 Memory Limit Exceeded 오류 발생
     * - 최대값은 maxMemoryLimit으로 제한됨
     */
    @Value("${judge0.execution.memory-limit:128000}")
    private Integer memoryLimit; // KB

    /**
     * 스택 메모리 제한 기본값 (KB 단위)
     * - 프로그램의 스택 영역 사용 제한
     * - 기본값: 64MB (64000KB)
     * - 재귀 호출이나 큰 지역 변수 사용 시 중요
     * - 최대값은 maxStackLimit으로 제한됨
     */
    @Value("${judge0.execution.stack-limit:64000}")
    private Integer stackLimit; // KB

    /**
     * 최대 프로세스/스레드 수 기본값
     * - 프로그램이 생성할 수 있는 최대 프로세스 및 스레드 수
     * - 기본값: 60개
     * - 멀티프로세싱, 멀티스레딩 프로그램의 리소스 제한
     * - 최대값은 maxMaxProcessesAndOrThreads로 제한됨
     */
    @Value("${judge0.execution.max-processes-and-or-threads:60}")
    private Integer maxProcessesAndOrThreads;

    /**
     * 최대 파일 크기 제한 기본값 (KB 단위)
     * - 프로그램이 생성할 수 있는 최대 파일 크기
     * - 기본값: 1MB (1024KB)
     * - 파일 출력이 많은 프로그램의 리소스 제한
     * - 최대값은 maxMaxFileSize로 제한됨
     */
    @Value("${judge0.execution.max-file-size:1024}")
    private Integer maxFileSize; // KB

    /**
     * 프로세스/스레드별 시간 제한 활성화 기본값
     * - 각 프로세스/스레드에 개별적으로 시간 제한 적용
     * - 기본값: false (비활성화)
     * - 멀티프로세싱 환경에서 개별 프로세스 제어
     */
    @Value("${judge0.execution.enable-per-process-and-thread-time-limit:false}")
    private Boolean enablePerProcessAndThreadTimeLimit;

    /**
     * 프로세스/스레드별 메모리 제한 활성화 기본값
     * - 각 프로세스/스레드에 개별적으로 메모리 제한 적용
     * - 기본값: false (비활성화)
     * - 멀티프로세싱 환경에서 개별 프로세스 메모리 제어
     */
    @Value("${judge0.execution.enable-per-process-and-thread-memory-limit:false}")
    private Boolean enablePerProcessAndThreadMemoryLimit;

    /**
     * 표준 오류를 표준 출력으로 리다이렉션 기본값
     * - stderr를 stdout으로 합쳐서 출력
     * - 기본값: false (분리 유지)
     * - 디버깅이나 로그 통합 시 유용
     */
    @Value("${judge0.execution.redirect-stderr-to-stdout:false}")
    private Boolean redirectStderrToStdout;

    // ========== 실행 제한 설정 - 최대값 (Execution Limits - Maximums) ==========
    
    /**
     * 다중 실행 횟수 최대값
     * - 사용자가 설정할 수 있는 최대 실행 횟수
     * - 기본값: 20회
     * - 시스템 리소스 보호를 위한 상한선
     * - 이 값을 초과하면 IllegalArgumentException 발생
     */
    @Value("${judge0.execution.max-number-of-runs:20}")
    private Integer maxNumberOfRuns;

    /**
     * CPU 실행 시간 제한 최대값 (초)
     * - 사용자가 설정할 수 있는 최대 CPU 시간
     * - 기본값: 15.0초
     * - 시스템 리소스 보호를 위한 상한선
     * - 이 값을 초과하면 IllegalArgumentException 발생
     */
    @Value("${judge0.execution.max-cpu-time-limit:15.0}")
    private BigDecimal maxCpuTimeLimit;

    /**
     * CPU 추가 여유 시간 최대값 (초)
     * - 사용자가 설정할 수 있는 최대 추가 시간
     * - 기본값: 5.0초
     * - 시스템 리소스 보호를 위한 상한선
     * - 이 값을 초과하면 IllegalArgumentException 발생
     */
    @Value("${judge0.execution.max-cpu-extra-time:5.0}")
    private BigDecimal maxCpuExtraTime;

    /**
     * 전체 실행 제한 시간 최대값 (초)
     * - 사용자가 설정할 수 있는 최대 전체 실행 시간
     * - 기본값: 20.0초
     * - 시스템 리소스 보호를 위한 상한선
     * - 이 값을 초과하면 IllegalArgumentException 발생
     */
    @Value("${judge0.execution.max-wall-time-limit:20.0}")
    private BigDecimal maxWallTimeLimit;

    /**
     * 메모리 사용 제한 최대값 (KB 단위)
     * - 사용자가 설정할 수 있는 최대 메모리 사용량
     * - 기본값: 512MB (512000KB)
     * - 시스템 리소스 보호를 위한 상한선
     * - 이 값을 초과하면 IllegalArgumentException 발생
     */
    @Value("${judge0.execution.max-memory-limit:512000}")
    private Integer maxMemoryLimit; // KB

    /**
     * 스택 메모리 제한 최대값 (KB 단위)
     * - 사용자가 설정할 수 있는 최대 스택 메모리 사용량
     * - 기본값: 128MB (128000KB)
     * - 시스템 리소스 보호를 위한 상한선
     * - 이 값을 초과하면 IllegalArgumentException 발생
     */
    @Value("${judge0.execution.max-stack-limit:128000}")
    private Integer maxStackLimit; // KB

    /**
     * 최대 프로세스/스레드 수 최대값
     * - 사용자가 설정할 수 있는 최대 프로세스/스레드 수
     * - 기본값: 120개
     * - 시스템 리소스 보호를 위한 상한선
     * - 이 값을 초과하면 IllegalArgumentException 발생
     */
    @Value("${judge0.execution.max-max-processes-and-or-threads:120}")
    private Integer maxMaxProcessesAndOrThreads;

    /**
     * 최대 파일 크기 제한 최대값 (KB 단위)
     * - 사용자가 설정할 수 있는 최대 파일 크기
     * - 기본값: 4MB (4096KB)
     * - 시스템 리소스 보호를 위한 상한선
     * - 이 값을 초과하면 IllegalArgumentException 발생
     */
    @Value("${judge0.execution.max-max-file-size:4096}")
    private Integer maxMaxFileSize; // KB

    /**
     * 최대 압축 해제 크기 (KB 단위)
     * - 추가 파일 압축 해제 시 최대 허용 크기
     * - 기본값: 10MB (10240KB)
     * - 악의적인 압축 파일로 인한 시스템 공격 방지
     * - 이 값을 초과하면 압축 해제 실패
     */
    @Value("${judge0.execution.max-extract-size:10240}")
    private Integer maxExtractSize; // KB

    // ========== 프로세스별 제한 제어 (Per-process Limits Control) ==========
    
    /**
     * 프로세스/스레드별 시간 제한 허용 여부
     * - true: 사용자가 프로세스/스레드별 시간 제한을 활성화할 수 있음 (기본값)
     * - false: 프로세스/스레드별 시간 제한 설정 비활성화
     * - 멀티프로세싱 환경에서 세밀한 제어가 필요할 때 사용
     */
    @Value("${judge0.execution.allow-enable-per-process-and-thread-time-limit:true}")
    private Boolean allowEnablePerProcessAndThreadTimeLimit;

    /**
     * 프로세스/스레드별 메모리 제한 허용 여부
     * - true: 사용자가 프로세스/스레드별 메모리 제한을 활성화할 수 있음 (기본값)
     * - false: 프로세스/스레드별 메모리 제한 설정 비활성화
     * - 멀티프로세싱 환경에서 세밀한 제어가 필요할 때 사용
     */
    @Value("${judge0.execution.allow-enable-per-process-and-thread-memory-limit:true}")
    private Boolean allowEnablePerProcessAndThreadMemoryLimit;

    // ========== 컴파일러 옵션 설정 (Compiler Options Configuration) ==========
    
    /**
     * 컴파일러 옵션 허용 언어 목록 (문자열)
     * - 컴파일러 옵션을 사용할 수 있는 언어들의 목록
     * - 공백으로 구분된 언어 이름들 (예: "C C++ Java")
     * - 빈 문자열이면 모든 언어에서 컴파일러 옵션 사용 가능
     * - 보안상 특정 언어에서만 컴파일러 옵션 허용하고 싶을 때 사용
     */
    @Value("${judge0.compiler.allowed-languages:}")
    private String allowedLanguagesForCompilerOptionsStr;

    // ========== 콜백 설정 (Callback Configuration) ==========
    
    /**
     * 콜백 재시도 최대 횟수
     * - 웹훅 콜백 전송 실패 시 재시도할 최대 횟수
     * - 기본값: 3회
     * - 네트워크 오류나 일시적 서버 문제로 인한 실패 대응
     * - 지수 백오프 방식으로 재시도 간격 증가
     */
    @Value("${judge0.callbacks.max-tries:3}")
    private Integer callbacksMaxTries;

    /**
     * 콜백 타임아웃 (초)
     * - 웹훅 콜백 전송 시 대기할 최대 시간
     * - 기본값: 5.0초
     * - 외부 서버 응답 지연으로 인한 무한 대기 방지
     * - 이 시간을 초과하면 콜백 실패로 처리
     */
    @Value("${judge0.callbacks.timeout:5.0}")
    private Double callbacksTimeout;

    // ========== 캐시 설정 (Cache Configuration) ==========
    
    /**
     * 제출 캐시 지속 시간 (초)
     * - 제출 결과를 캐시에 보관할 시간
     * - 기본값: 1.0초
     * - 동일한 제출에 대한 반복 조회 시 성능 향상
     * - 메모리 사용량과 성능의 균형을 위한 설정
     */
    @Value("${judge0.cache.submission-cache-duration:1.0}")
    private Double submissionCacheDuration;

    // ========== Docker 설정 (Docker Configuration) ==========
    
    /**
     * Docker 데몬 호스트 주소
     * - Docker 데몬과 통신할 주소
     * - 기본값: unix:///var/run/docker.sock (Unix 소켓)
     * - TCP 연결 시: tcp://localhost:2375
     * - Docker 컨테이너 생성/관리를 위한 연결 정보
     */
    @Value("${judge0.docker.host:unix:///var/run/docker.sock}")
    private String dockerHost;

    /**
     * Docker 레지스트리 URL
     * - Docker 이미지를 가져올 레지스트리 주소
     * - 기본값: docker.io (Docker Hub)
     * - 프라이빗 레지스트리 사용 시 변경
     * - 이미지 풀링 시 사용되는 기본 레지스트리
     */
    @Value("${judge0.docker.registry-url:docker.io}")
    private String dockerRegistryUrl;

    /**
     * 최대 동시 컨테이너 수
     * - 동시에 실행할 수 있는 최대 컨테이너 수
     * - 기본값: 10개
     * - 시스템 리소스와 성능의 균형을 위한 제한
     * - 이 수를 초과하면 대기열에서 대기
     */
    @Value("${judge0.docker.max-concurrent-containers:10}")
    private Integer maxConcurrentContainers;

    /**
     * 컨테이너 타임아웃 (초)
     * - 컨테이너 실행 후 강제 종료까지의 최대 시간
     * - 기본값: 300초 (5분)
     * - 무한 루프나 응답 없는 컨테이너 방지
     * - 이 시간을 초과하면 컨테이너 강제 종료
     */
    @Value("${judge0.docker.container-timeout:300}")
    private Integer containerTimeout; // seconds

    // ========== 보안 설정 (Security Configuration) ==========
    
    /**
     * 샌드박스 사용자명
     * - Docker 컨테이너 내에서 코드를 실행할 사용자명
     * - 기본값: judge
     * - 권한이 제한된 사용자로 실행하여 보안 강화
     * - 시스템 파일 접근이나 권한 상승 공격 방지
     */
    @Value("${judge0.security.sandbox-user:judge}")
    private String sandboxUser;

    /**
     * 허용된 시스템 호출 목록 (문자열)
     * - 컨테이너 내에서 사용할 수 있는 시스템 호출들
     * - 기본값: read,write,exit,exit_group
     * - 쉼표로 구분된 시스템 호출 이름들
     * - 보안상 위험한 시스템 호출 차단을 위한 화이트리스트
     */
    @Value("${judge0.security.allowed-syscalls:read,write,exit,exit_group}")
    private String allowedSyscallsStr;

    // ========== 유틸리티 메서드 (Utility Methods) ==========
    
    /**
     * 컴파일러 옵션 허용 언어 목록을 리스트로 반환
     * - 설정된 문자열을 공백으로 분리하여 리스트로 변환
     * - 빈 문자열이거나 null인 경우 빈 리스트 반환
     * - 보안 검증 시 사용되는 언어 목록 제공
     * 
     * @return 허용된 언어 이름들의 리스트
     */
    public List<String> getAllowedLanguagesForCompilerOptions() {
        if (allowedLanguagesForCompilerOptionsStr == null || allowedLanguagesForCompilerOptionsStr.trim().isEmpty()) {
            return List.of();
        }
        return Arrays.asList(allowedLanguagesForCompilerOptionsStr.trim().split("\\s+"));
    }

    /**
     * 허용된 시스템 호출 목록을 리스트로 반환
     * - 설정된 문자열을 쉼표로 분리하여 리스트로 변환
     * - 빈 문자열이거나 null인 경우 기본 시스템 호출 목록 반환
     * - Docker 컨테이너 보안 정책 설정 시 사용
     * 
     * @return 허용된 시스템 호출 이름들의 리스트
     */
    public List<String> getAllowedSyscalls() {
        if (allowedSyscallsStr == null || allowedSyscallsStr.trim().isEmpty()) {
            return List.of("read", "write", "exit", "exit_group");
        }
        return Arrays.asList(allowedSyscallsStr.split(","));
    }

    /**
     * 전체 설정 정보를 Map 형태로 반환
     * - API 응답이나 관리자 페이지에서 사용
     * - 모든 설정값을 키-값 쌍으로 구성하여 반환
     * - 클라이언트가 현재 시스템 설정을 확인할 수 있도록 제공
     * 
     * @return 설정 정보가 담긴 Map 객체
     */
    public Map<String, Object> getConfigInfo() {
        Map<String, Object> config = new HashMap<>();
        config.put("maintenance_mode", maintenanceMode);
        config.put("enable_wait_result", enableWaitResult);
        config.put("enable_compiler_options", compilerOptionsEnabled);
        config.put("allowed_languages_for_compiler_options", getAllowedLanguagesForCompilerOptions());
        config.put("enable_command_line_arguments", commandLineArgumentsEnabled);
        config.put("enable_submission_delete", submissionDeleteEnabled);
        config.put("enable_callbacks", callbacksEnabled);
        config.put("callbacks_max_tries", callbacksMaxTries);
        config.put("callbacks_timeout", callbacksTimeout);
        config.put("enable_additional_files", additionalFilesEnabled);
        config.put("max_queue_size", maxQueueSize);
        config.put("cpu_time_limit", cpuTimeLimit);
        config.put("max_cpu_time_limit", maxCpuTimeLimit);
        config.put("cpu_extra_time", cpuExtraTime);
        config.put("max_cpu_extra_time", maxCpuExtraTime);
        config.put("wall_time_limit", wallTimeLimit);
        config.put("max_wall_time_limit", maxWallTimeLimit);
        config.put("memory_limit", memoryLimit);
        config.put("max_memory_limit", maxMemoryLimit);
        config.put("stack_limit", stackLimit);
        config.put("max_stack_limit", maxStackLimit);
        config.put("max_processes_and_or_threads", maxProcessesAndOrThreads);
        config.put("max_max_processes_and_or_threads", maxMaxProcessesAndOrThreads);
        config.put("enable_per_process_and_thread_time_limit", enablePerProcessAndThreadTimeLimit);
        config.put("allow_enable_per_process_and_thread_time_limit", allowEnablePerProcessAndThreadTimeLimit);
        config.put("enable_per_process_and_thread_memory_limit", enablePerProcessAndThreadMemoryLimit);
        config.put("allow_enable_per_process_and_thread_memory_limit", allowEnablePerProcessAndThreadMemoryLimit);
        config.put("max_file_size", maxFileSize);
        config.put("max_max_file_size", maxMaxFileSize);
        config.put("number_of_runs", numberOfRuns);
        config.put("max_number_of_runs", maxNumberOfRuns);
        config.put("redirect_stderr_to_stdout", redirectStderrToStdout);
        config.put("max_extract_size", maxExtractSize);
        config.put("enable_batched_submissions", batchedSubmissionsEnabled);
        config.put("max_submission_batch_size", maxSubmissionBatchSize);
        config.put("submission_cache_duration", submissionCacheDuration);
        config.put("allow_enable_network", networkAllowed);
        config.put("enable_network", enableNetwork);
        config.put("disable_implicit_base64_encoding", disableImplicitBase64Encoding);
        return config;
    }

    /**
     * 실행 제한값 유효성 검증
     * - 사용자가 설정한 실행 제한값들이 시스템 최대값을 초과하지 않는지 검증
     * - 각 제한값이 유효한 범위 내에 있는지 확인
     * - 유효하지 않은 값이 있으면 IllegalArgumentException 발생
     * - 현재는 정의되어 있지만 실제로는 SecurityManager에서 비슷한 검증 수행
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
        if (numberOfRuns != null && (numberOfRuns < 1 || numberOfRuns > maxNumberOfRuns)) {
            throw new IllegalArgumentException("Number of runs must be between 1 and " + maxNumberOfRuns);
        }

        // CPU 실행 시간 제한 검증: 0초 이상, 최대값 이하
        if (cpuTimeLimit != null && (cpuTimeLimit.compareTo(BigDecimal.ZERO) < 0 || cpuTimeLimit.compareTo(maxCpuTimeLimit) > 0)) {
            throw new IllegalArgumentException("CPU time limit must be between 0 and " + maxCpuTimeLimit + " seconds");
        }

        // CPU 추가 여유 시간 검증: 0초 이상, 최대값 이하
        if (cpuExtraTime != null && (cpuExtraTime.compareTo(BigDecimal.ZERO) < 0 || cpuExtraTime.compareTo(maxCpuExtraTime) > 0)) {
            throw new IllegalArgumentException("CPU extra time must be between 0 and " + maxCpuExtraTime + " seconds");
        }

        // 전체 실행 제한 시간 검증: 1초 이상, 최대값 이하
        if (wallTimeLimit != null && (wallTimeLimit.compareTo(BigDecimal.ONE) < 0 || wallTimeLimit.compareTo(maxWallTimeLimit) > 0)) {
            throw new IllegalArgumentException("Wall time limit must be between 1 and " + maxWallTimeLimit + " seconds");
        }

        // 메모리 사용 제한 검증: 2MB 이상, 최대값 이하
        if (memoryLimit != null && (memoryLimit < 2048 || memoryLimit > maxMemoryLimit)) {
            throw new IllegalArgumentException("Memory limit must be between 2048 and " + maxMemoryLimit + " KB");
        }

        // 스택 메모리 제한 검증: 0KB 이상, 최대값 이하
        if (stackLimit != null && (stackLimit < 0 || stackLimit > maxStackLimit)) {
            throw new IllegalArgumentException("Stack limit must be between 0 and " + maxStackLimit + " KB");
        }

        // 최대 프로세스/스레드 수 검증: 1개 이상, 최대값 이하
        if (maxProcesses != null && (maxProcesses < 1 || maxProcesses > maxMaxProcessesAndOrThreads)) {
            throw new IllegalArgumentException("Max processes must be between 1 and " + maxMaxProcessesAndOrThreads);
        }

        // 최대 파일 크기 검증: 0KB 이상, 최대값 이하
        if (maxFileSize != null && (maxFileSize < 0 || maxFileSize > maxMaxFileSize)) {
            throw new IllegalArgumentException("Max file size must be between 0 and " + maxMaxFileSize + " KB");
        }
    }
}
