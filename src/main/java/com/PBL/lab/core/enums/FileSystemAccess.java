package com.PBL.lab.core.enums;

/**
 * 파일 시스템 접근 권한 열거형
 *
 * Docker 컨테이너 내에서 파일 시스템에 대한 접근 권한을 정의합니다.
 * 보안을 위해 기본적으로는 READ_ONLY를 사용하며, 필요시에만 READ_WRITE를 허용합니다.
 *
 * 권한 종류:
 * - READ_ONLY: 읽기 전용 (기본값, 보안상 안전)
 * - READ_WRITE: 읽기/쓰기 허용 (특별한 경우에만 사용)
 */
public enum FileSystemAccess {
    READ_ONLY,    // 읽기 전용 (기본값)
    READ_WRITE    // 읽기/쓰기 허용
}
