package com.PBL.lab.core.dto;

import com.github.dockerjava.api.model.HostConfig;
import lombok.Builder;
import lombok.Data;

/**
 * Docker 컨테이너 생성 설정 DTO
 *
 * Docker 컨테이너를 생성할 때 필요한 모든 설정 정보를 담는 데이터 전송 객체입니다.
 * ContainerManager에서 컨테이너 생성 시 사용됩니다.
 */
@Builder
@Data
public class ContainerConfig {

    /** Docker 이미지 이름 (예: judge0/compilers, python:3.11-slim) */
    private String image;

    /** 컨테이너 이름 (예: judge0-exec-abc123) */
    private String name;

    /** 작업 디렉토리 (예: /tmp/judge) */
    private String workingDir;

    /** 실행 사용자 (예: nobody:nogroup, root) */
    private String user;

    /** 실행할 명령어 배열 (예: ["tail", "-f", "/dev/null"]) */
    private String[] command;

    /** 환경변수 목록 (예: ["PATH=/usr/bin", "LANG=en_US.UTF-8"]) */
    private String[] environment;

    /** 호스트 설정 (리소스 제한, 마운트 등) */
    private HostConfig hostConfig;

    /** 네트워크 모드 (예: none, bridge) */
    private String networkMode;
}
