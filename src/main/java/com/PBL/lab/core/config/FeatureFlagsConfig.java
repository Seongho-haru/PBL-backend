package com.PBL.lab.core.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Feature Flags Configuration
 *
 * Judge0 시스템의 기능 플래그 설정을 관리합니다.
 * 각 기능의 활성화/비활성화를 제어하여 시스템 보안과 성능을 관리합니다.
 *
 * 주요 기능:
 * - 동기/비동기 실행 제어
 * - 컴파일러 옵션 허용 여부
 * - 명령행 인수 허용 여부
 * - 제출 삭제 기능 제어
 * - 웹훅 콜백 기능 제어
 * - 추가 파일 업로드 제어
 * - 배치 제출 기능 제어
 * - 네트워크 접근 제어
 * - Base64 인코딩 제어
 *
 * 사용 시나리오:
 * - 보안상 위험한 기능 차단
 * - 서버 부하 시 일부 기능 비활성화
 * - 단계적 기능 롤아웃
 */
@Component
@ConfigurationProperties(prefix = "judge0.features")
@Getter
@Setter
public class FeatureFlagsConfig {

    /**
     * 동기 실행 결과 대기 기능 활성화 여부
     * - true: wait=true 파라미터로 동기 실행 결과 대기 허용 (기본값)
     * - false: 동기 실행 비활성화, 모든 제출은 비동기로만 처리
     * - 대용량 트래픽 시 서버 부하 방지를 위해 비활성화 가능
     */
    private boolean enableWaitResult = true;

    /**
     * 컴파일러 옵션 설정 기능 활성화 여부
     * - true: 사용자가 컴파일러 옵션을 설정할 수 있음 (기본값)
     * - false: 컴파일러 옵션 설정 비활성화
     * - 보안상 위험한 컴파일러 옵션 사용을 방지하기 위해 비활성화 가능
     */
    private boolean enableCompilerOptions = true;

    /**
     * 명령행 인수 설정 기능 활성화 여부
     * - true: 사용자가 프로그램 실행 시 명령행 인수를 설정할 수 있음 (기본값)
     * - false: 명령행 인수 설정 비활성화
     * - 보안상 위험한 명령행 인수 사용을 방지하기 위해 비활성화 가능
     */
    private boolean enableCommandLineArguments = true;

    /**
     * 제출 삭제 기능 활성화 여부
     * - true: 사용자가 제출한 코드를 삭제할 수 있음
     * - false: 제출 삭제 비활성화 (기본값, 보안상 안전)
     * - 데이터 보존을 위해 기본적으로 비활성화
     */
    private boolean enableSubmissionDelete = false;

    /**
     * 웹훅 콜백 기능 활성화 여부
     * - true: 실행 완료 시 외부 URL로 결과 전송 가능 (기본값)
     * - false: 웹훅 콜백 비활성화
     * - 외부 시스템과의 연동이 필요할 때 활성화
     */
    private boolean enableCallbacks = true;

    /**
     * 추가 파일 업로드 기능 활성화 여부
     * - true: 프로젝트 제출 시 추가 파일 업로드 가능 (기본값)
     * - false: 추가 파일 업로드 비활성화
     * - 다중 파일 프로젝트 지원을 위해 필요
     */
    private boolean enableAdditionalFiles = true;

    /**
     * 배치 제출 기능 활성화 여부
     * - true: 여러 제출을 한 번에 처리하는 배치 API 사용 가능 (기본값)
     * - false: 배치 제출 비활성화
     * - 대량 제출 처리 시 성능 향상을 위해 사용
     */
    private boolean enableBatchedSubmissions = true;

    /**
     * 네트워크 접근 허용 설정 기능 활성화 여부
     * - true: 사용자가 네트워크 접근을 허용할 수 있음 (기본값)
     * - false: 네트워크 접근 설정 비활성화
     * - 보안상 네트워크 접근을 완전히 차단하고 싶을 때 사용
     */
    private boolean allowEnableNetwork = true;

    /**
     * 네트워크 접근 기본 활성화 여부
     * - true: 기본적으로 네트워크 접근 허용
     * - false: 기본적으로 네트워크 접근 차단 (기본값, 보안상 안전)
     * - 외부 API 호출이 필요한 코드 실행 시에만 허용
     */
    private boolean enableNetwork = false;

    /**
     * 암시적 Base64 인코딩 비활성화 여부
     * - true: 자동 Base64 인코딩/디코딩 비활성화
     * - false: 자동 Base64 인코딩/디코딩 활성화 (기본값)
     * - 특수 문자나 바이너리 데이터 처리를 위해 사용
     */
    private boolean disableImplicitBase64Encoding = false;

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
}
