package com.PBL.lab.core.dto;

import com.github.dockerjava.api.model.Statistics;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Docker 컨테이너 통계 정보 DTO
 *
 * 컨테이너의 리소스 사용량 통계를 담는 데이터 전송 객체입니다.
 * 메모리 사용량, CPU 사용률, 네트워크 송수신량 등을 포함합니다.
 */
@Data
@AllArgsConstructor
public class ContainerStats {

    /** 메모리 사용량 (바이트) */
    private final Long memoryUsage;

    /** CPU 사용률 (퍼센트, 0.0~100.0) */
    private final Double cpuPercent;

    /** 네트워크 수신 바이트 */
    private final Long networkRx;

    /** 네트워크 송신 바이트 */
    private final Long networkTx;

    /**
     * Docker Statistics 객체에서 ContainerStats 생성
     *
     * Docker Java API의 Statistics 객체를 우리 프로젝트의 DTO로 변환합니다.
     *
     * @param stats Docker Statistics 객체
     * @return ContainerStats 객체
     */
    public static ContainerStats from(Statistics stats) {
        if (stats == null) {
            return new ContainerStats(null, null, null, null);
        }

        Long memoryUsage = stats.getMemoryStats() != null ?
            stats.getMemoryStats().getUsage() : null;

        // CPU 퍼센트 계산은 실제 구현에서 더 복잡해질 수 있음
        Double cpuPercent = null;

        Long networkRx = null;
        Long networkTx = null;

        return new ContainerStats(memoryUsage, cpuPercent, networkRx, networkTx);
    }
}
