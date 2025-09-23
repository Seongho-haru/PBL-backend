package com.PBL.lab.judge0.repository;

import com.PBL.lab.judge0.entity.Submission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Submission Repository
 * 
 * Data access layer for Submission entity.
 * Provides custom query methods for submission management.
 */
@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    /**
     * Find submission by token
     */
    Optional<Submission> findByToken(String token);

    /**
     * Find submission by token with all related entities (constraints and inputOutput)
     * This method uses JOIN FETCH to avoid LazyInitializationException
     */
    @Query("SELECT s FROM Submission s " +
           "LEFT JOIN FETCH s.constraints " +
           "LEFT JOIN FETCH s.inputOutput " +
           "LEFT JOIN FETCH s.language " +
           "WHERE s.token = :token")
    Optional<Submission> findByTokenWithRelations(@Param("token") String token);

    /**
     * Find submissions by status ID
     */
    List<Submission> findByStatusId(Integer statusId);

    /**
     * Find submissions by status ID with pagination
     */
    Page<Submission> findByStatusId(Integer statusId, Pageable pageable);

    /**
     * Find submissions created between dates
     */
    List<Submission> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Count submissions by status ID
     */
    Long countByStatusId(Integer statusId);

    /**
     * Find submissions by language ID
     */
    List<Submission> findByLanguageId(Integer languageId);

    /**
     * Find submissions by language ID with pagination
     */
    Page<Submission> findByLanguageId(Integer languageId, Pageable pageable);

    /**
     * Find submissions by multiple tokens
     */
    @Query("SELECT s FROM Submission s WHERE s.token IN :tokens")
    List<Submission> findByTokenIn(@Param("tokens") List<String> tokens);

    /**
     * Find submissions that are in queue (status_id = 1)
     */
    @Query("SELECT s FROM Submission s WHERE s.statusId = 1 ORDER BY s.createdAt ASC")
    List<Submission> findSubmissionsInQueue();

    /**
     * Find submissions that are processing (status_id = 2)
     */
    @Query("SELECT s FROM Submission s WHERE s.statusId = 2")
    List<Submission> findProcessingSubmissions();

    /**
     * Find submissions that can be deleted (finished and not in queue/processing)
     */
    @Query("SELECT s FROM Submission s WHERE s.statusId NOT IN (1, 2)")
    List<Submission> findDeletableSubmissions();

    /**
     * Count submissions in queue
     */
    @Query("SELECT COUNT(s) FROM Submission s WHERE s.statusId = 1")
    Long countSubmissionsInQueue();

    /**
     * Count submissions by date range
     */
    @Query("SELECT COUNT(s) FROM Submission s WHERE s.createdAt BETWEEN :start AND :end")
    Long countSubmissionsByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /**
     * Find submissions by execution host
     */
    List<Submission> findByExecutionHost(String executionHost);

    /**
     * Find submissions by queue host
     */
    List<Submission> findByQueueHost(String queueHost);

    /**
     * Find submissions with callback URL that failed
     */
    @Query("SELECT s FROM Submission s JOIN s.constraints c WHERE c.callbackUrl IS NOT NULL AND s.statusId NOT IN (1, 2)")
    List<Submission> findSubmissionsWithCallback();

    /**
     * Find recent submissions (last 24 hours)
     */
    @Query("SELECT s FROM Submission s WHERE s.createdAt >= :since ORDER BY s.createdAt DESC")
    List<Submission> findRecentSubmissions(@Param("since") LocalDateTime since);

    /**
     * Find submissions by status ID and created after
     */
    @Query("SELECT s FROM Submission s WHERE s.statusId = :statusId AND s.createdAt >= :since")
    List<Submission> findByStatusIdAndCreatedAtAfter(@Param("statusId") Integer statusId, @Param("since") LocalDateTime since);

    /**
     * Check if token exists
     */
    boolean existsByToken(String token);

    /**
     * Delete submissions older than specified date
     */
    @Query("DELETE FROM Submission s WHERE s.createdAt < :before")
    void deleteSubmissionsOlderThan(@Param("before") LocalDateTime before);

    /**
     * Find submissions ordered by creation date (default order)
     */
    @Query("SELECT s FROM Submission s ORDER BY s.createdAt DESC")
    Page<Submission> findAllOrderedByCreatedAt(Pageable pageable);

    /**
     * Count total submissions
     */
    @Query("SELECT COUNT(s) FROM Submission s")
    Long countTotalSubmissions();

    /**
     * Count submissions by status ID between range
     */
    @Query("SELECT COUNT(s) FROM Submission s WHERE s.statusId BETWEEN :start AND :end")
    Long countByStatusIdBetween(@Param("start") Integer start, @Param("end") Integer end);

    /**
     * Find submissions statistics
     */
    @Query("SELECT s.statusId, COUNT(s) FROM Submission s GROUP BY s.statusId")
    List<Object[]> findSubmissionStatistics();
}
