package com.PBL.lab.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Docker 컨테이너 스크립트 실행 결과 DTO
 *
 * 컨테이너 내에서 bash 스크립트를 실행한 결과를 담는 데이터 전송 객체입니다.
 * 표준 출력, 표준 에러, 종료 코드, 완료 여부를 포함합니다.
 */
@Data
@AllArgsConstructor
public class ExecResult {

    /** 표준 출력 (stdout) 내용 */
    private String stdout;

    /** 표준 에러 (stderr) 내용 */
    private String stderr;

    /** 프로세스 종료 코드 (0: 성공, 1~: 오류) */
    private Integer exitCode;

    /** 시간 제한 내에 완료되었는지 여부 */
    private boolean completed;

    /**
     * 실행 중 오류가 발생했는지 확인
     *
     * @return 종료 코드가 0이 아니면 true (오류 발생)
     */
    public boolean hasError() {
        return exitCode != null && exitCode != 0;
    }
}
