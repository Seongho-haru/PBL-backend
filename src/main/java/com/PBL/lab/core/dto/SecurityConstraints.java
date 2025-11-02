package com.PBL.lab.core.dto;

import com.PBL.lab.core.enums.FileSystemAccess;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 보안 제약조건 데이터 클래스
 *
 * Docker 컨테이너에서 코드 실행 시 적용되는 보안 및 리소스 제한사항을 정의합니다.
 * 이 제약조건들은 악성 코드나 무한 루프 등으로부터 시스템을 보호하는 역할을 합니다.
 *
 * 제약조건 종류:
 * - timeLimit: CPU 시간 제한 (초 단위)
 * - memoryLimit: 메모리 사용량 제한 (KB 단위)
 * - processLimit: 최대 프로세스/스레드 수 제한
 * - networkAccess: 네트워크 접근 허용 여부
 * - fileSystemAccess: 파일 시스템 접근 권한 (읽기 전용/읽기 쓰기)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecurityConstraints {
    private BigDecimal timeLimit;
    private Integer memoryLimit;
    private Integer processLimit;
    private Boolean networkAccess;
    private FileSystemAccess fileSystemAccess;
}
