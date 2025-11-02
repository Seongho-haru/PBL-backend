package com.PBL.lab.core.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * System Configuration
 *
 * Judge0 시스템의 기본 운영 설정을 관리합니다.
 * 여러 작은 설정들을 하나로 통합하여 관리 효율성을 높였습니다.
 *
 * 포함된 설정:
 * 1. Maintenance - 유지보수 모드
 * 2. Queue - 대기열 크기 제한
 * 3. Cache - 캐시 지속 시간
 * 4. Security - 샌드박스 보안
 * 5. Callbacks - 웹훅 콜백
 * 6. Compiler Options - 컴파일러 옵션 제한
 */
@Component
@ConfigurationProperties(prefix = "judge0.system")
@Getter
@Setter
public class SystemConfig {

    // ========== Maintenance Configuration ==========

    /**
     * 유지보수 모드 활성화 여부
     * - true: 시스템이 유지보수 모드로 전환되어 새로운 제출을 받지 않음
     * - false: 정상 운영 모드 (기본값)
     */
    private boolean maintenanceMode = false;

    /**
     * 유지보수 모드 시 표시할 메시지
     * - 유지보수 모드가 활성화되었을 때 클라이언트에게 보여줄 안내 메시지
     */
    private String maintenanceMessage = "Judge0 is currently in maintenance.";

    // ========== Queue Configuration ==========

    /**
     * 최대 대기열 크기
     * - 시스템이 동시에 처리할 수 있는 최대 제출 수
     * - 기본값: 100개
     */
    private Integer maxQueueSize = 100;

    /**
     * 최대 배치 제출 크기
     * - 한 번에 처리할 수 있는 최대 제출 수
     * - 기본값: 20개
     */
    private Integer maxSubmissionBatchSize = 20;

    // ========== Cache Configuration ==========

    /**
     * 제출 캐시 지속 시간 (초)
     * - 제출 결과를 캐시에 보관할 시간
     * - 기본값: 1.0초
     */
    private Double submissionCacheDuration = 1.0;

    // ========== Security Configuration ==========

    /**
     * 샌드박스 사용자명
     * - Docker 컨테이너 내에서 코드를 실행할 사용자명
     * - 기본값: judge
     */
    private String sandboxUser = "judge";

    /**
     * 허용된 시스템 호출 목록 (문자열)
     * - 컨테이너 내에서 사용할 수 있는 시스템 호출들
     * - 기본값: read,write,exit,exit_group
     * - 쉼표로 구분
     */
    private String allowedSyscalls = "read,write,exit,exit_group";

    // ========== Compiler Options Configuration ==========

    /**
     * 컴파일러 옵션 허용 언어 목록 (문자열)
     * - 컴파일러 옵션을 사용할 수 있는 언어들의 목록
     * - 공백으로 구분 (예: "C C++ Java")
     * - 빈 문자열이면 모든 언어에서 사용 가능
     */
    private String allowedLanguagesForCompilerOptions = "";

    // ========== Callbacks Configuration ==========

    /**
     * 콜백 재시도 최대 횟수
     * - 웹훅 콜백 전송 실패 시 재시도할 최대 횟수
     * - 기본값: 3회
     */
    private Integer callbacksMaxTries = 3;

    /**
     * 콜백 타임아웃 (초)
     * - 웹훅 콜백 전송 시 대기할 최대 시간
     * - 기본값: 5.0초
     */
    private Double callbacksTimeout = 5.0;

    // ========== Utility Methods ==========

    /**
     * 허용된 시스템 호출 목록을 리스트로 반환
     *
     * @return 허용된 시스템 호출 이름들의 리스트
     */
    public List<String> getAllowedSyscalls() {
        if (allowedSyscalls == null || allowedSyscalls.trim().isEmpty()) {
            return List.of("read", "write", "exit", "exit_group");
        }
        return Arrays.asList(allowedSyscalls.split(","));
    }

    /**
     * 컴파일러 옵션 허용 언어 목록을 리스트로 반환
     *
     * @return 허용된 언어 이름들의 리스트
     */
    public List<String> getAllowedLanguagesForCompilerOptions() {
        if (allowedLanguagesForCompilerOptions == null || allowedLanguagesForCompilerOptions.trim().isEmpty()) {
            return List.of();
        }
        return Arrays.asList(allowedLanguagesForCompilerOptions.trim().split("\\s+"));
    }
}
