package com.PBL.lab.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Language Entity - 프로그래밍 언어 데이터 모델 엔티티
 * 
 * 목적:
 * - Judge0에서 지원하는 프로그래밍 언어를 나타내는 JPA 엔티티
 * - 원본 Judge0 Ruby Language 모델의 완전한 Java/JPA 포팅
 * - 언어별 실행 환경, 컴파일 설정, 자원 제한 관리
 * 
 * 핵심 언어 정보:
 * - name: 언어 이름 (예: "C (GCC 9.2.0)", "Python (3.8.1)", "Java (OpenJDK 13.0.1)")
 * - id: 언어 고유 식별자 (수치, 자동 증가)
 * - isArchived: 사용 중단 여부 (비활성화된 언어 표시)
 * 
 * 실행 명령어 설정:
 * - compileCmd: 컴파일 명령어 (C, C++, Java 등 컴파일 언어에만 존재)
 * - runCmd: 실행 명령어 (모든 언어에 필수)
 * - sourceFile: 소스 코드 파일 이름 (예: "main.c", "Main.java", "script.py")
 * 
 * 언어 분류 및 특성:
 * - 컴파일 언어: compileCmd가 있는 언어 (C, C++, Java, C#, Go 등)
 * - 인터프리터 언어: compileCmd가 없는 언어 (Python, JavaScript, Ruby 등)
 * - 프로젝트 언어: "Multi-file program" 이름을 가진 특수 언어
 * 
 * 자원 제한 기본값:
 * - timeLimit: 언어별 기본 실행 시간 제한 (초 단위, 기본 5.0초)
 * - memoryLimit: 언어별 기본 메모리 제한 (KB 단위, 기본 128000KB)
 * - 언어 특성에 맞는 최적화된 제한값 설정 가능
 * 
 * Docker 컨테이너 설정:
 * - dockerImage: 언어별 전용 Docker 이미지 (선택사항)
 * - dockerCompileCommand: 컨테이너 내에서 사용할 컴파일 명령어
 * - dockerRunCommand: 컨테이너 내에서 사용할 실행 명령어
 * 
 * == 데이터베이스 스키마 정보 ==
 * 테이블명: languages
 * 필수 필드: id(자동증가), name(비어있지않음)
 * 선택 필드: compile_cmd, run_cmd, source_file, docker 관련 필드들
 * 기본값: is_archived=false, time_limit=5.0, memory_limit=128000
 */
@Entity
@Table(name = "languages")
@Data
public class Language {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Column(name = "compile_cmd")
    private String compileCmd;

    @Column(name = "run_cmd")
    private String runCmd;

    @Column(name = "source_file")
    private String sourceFile;

    @Column(name = "is_archived")
    private Boolean isArchived = false;

    // Default limits for this language
    @Column(name = "time_limit", precision = 10, scale = 6)
    private BigDecimal timeLimit = BigDecimal.valueOf(5.0);

    @Column(name = "memory_limit")
    private Integer memoryLimit = 128000; // KB

    // Docker configuration
    @Column(name = "docker_image")
    private String dockerImage;

    @Column(name = "docker_compile_command")
    private String dockerCompileCommand;

    @Column(name = "docker_run_command")
    private String dockerRunCommand;

    // Helper methods
    public boolean isProject() {
        return "Multi-file program".equals(this.name);
    }

    public boolean supportsCompilation() {
        return compileCmd != null && !compileCmd.trim().isEmpty();
    }

    public boolean isEnabled() {
        return !Boolean.TRUE.equals(isArchived);
    }

    public String getEffectiveDockerImage() {
        if (dockerImage != null && !dockerImage.trim().isEmpty()) {
            return dockerImage;
        }
        
        // 로컬에 이미 다운로드된 judge0/compilers 이미지 사용
        // :latest 제거하여 매번 인터넷 확인 방지
        return "judge0/compilers";
    }

    public String getEffectiveCompileCommand() {
        if (dockerCompileCommand != null && !dockerCompileCommand.trim().isEmpty()) {
            return dockerCompileCommand;
        }
        return compileCmd;
    }

    public String getEffectiveRunCommand() {
        if (dockerRunCommand != null && !dockerRunCommand.trim().isEmpty()) {
            return dockerRunCommand;
        }
        return runCmd;
    }
}
