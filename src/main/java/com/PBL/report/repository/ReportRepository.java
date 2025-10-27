package com.PBL.report.repository;

import com.PBL.report.entity.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 신고 Repository
 */
public interface ReportRepository extends JpaRepository<Report, Long> {

    /**
     * 상태별 신고 목록 조회
     */
    Page<Report> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);

    /**
     * 타입별 신고 목록 조회
     */
    Page<Report> findByTargetTypeOrderByCreatedAtDesc(String targetType, Pageable pageable);

    /**
     * 상태 및 타입별 신고 목록 조회
     */
    Page<Report> findByStatusAndTargetTypeOrderByCreatedAtDesc(String status, String targetType, Pageable pageable);

    /**
     * 사용자가 작성한 신고 목록 조회
     */
    List<Report> findByReporterIdOrderByCreatedAtDesc(Long reporterId);

    /**
     * 특정 타입 및 대상 ID의 신고 개수 조회
     */
    Long countByTargetTypeAndTargetId(String targetType, Long targetId);

    /**
     * 특정 사용자가 특정 대상을 신고했는지 확인
     */
    boolean existsByReporterIdAndTargetTypeAndTargetId(Long reporterId, String targetType, Long targetId);

    /**
     * 날짜 범위별 신고 조회
     */
    @Query("SELECT r FROM Report r WHERE r.createdAt BETWEEN :startDate AND :endDate ORDER BY r.createdAt DESC")
    List<Report> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * 처리된 신고 조회 (RESOLVED 또는 REJECTED)
     */
    Page<Report> findByStatusInOrderByProcessedAtDesc(List<String> statuses, Pageable pageable);
}

