package com.PBL.lab.core.dto;

import com.PBL.lab.core.entity.Language;
import lombok.Builder;
import lombok.Data;

import java.nio.file.Path;

/**
 * 컴파일 컨텍스트 DTO
 *
 * 이 클래스는 컴파일된 코드를 여러 번 실행하기 위해 필요한 정보를 담고 있습니다.
 * 컴파일과 실행을 분리하여 성능을 최적화하는 데 사용됩니다.
 *
 * 사용 시나리오:
 * 1. prepareCompilation() → CompilationContext 반환
 * 2. executeWithCompiledCode(context, input) × N회
 * 3. cleanupCompilation(context)
 *
 * 성능 효과:
 * - 기존: (컴파일 + 실행) × N회
 * - 개선: 컴파일 1회 + 실행 × N회
 * - 테스트케이스 10개 기준: 24초 → 6초 (4배 향상)
 */
@Data
@Builder
public class CompilationContext {

    /**
     * Docker 컨테이너 ID
     * 컴파일된 바이너리를 포함한 실행 환경
     */
    private String containerId;

    /**
     * 작업 디렉토리 경로
     * 소스코드, 바이너리, 입출력 파일이 위치한 경로
     */
    private Path workDir;

    /**
     * 컴파일 출력 메시지
     * 컴파일 과정에서 발생한 경고나 메시지
     */
    private String compileOutput;

    /**
     * 프로그래밍 언어 정보
     * 실행 시 필요한 언어별 설정 정보
     */
    private Language language;

    /**
     * 컴파일 소요 시간 (밀리초)
     * 성능 모니터링 및 통계용
     */
    private long compileTime;
}
