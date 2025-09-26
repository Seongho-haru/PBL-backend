package com.PBL.lab.core.repository;

import com.PBL.lab.core.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Language Repository
 * 
 * Data access layer for Language entity.
 * Provides custom query methods for language management.
 */
@Repository
public interface LanguageRepository extends JpaRepository<Language, Integer> {

    /**
     * Find language by name
     */
    Optional<Language> findByName(String name);

    /**
     * Find all enabled languages (not archived)
     */
    @Query("SELECT l FROM Language l WHERE l.isArchived = false ORDER BY l.name ASC")
    List<Language> findAllEnabled();

    /**
     * Find all languages including archived ones
     */
    @Query("SELECT l FROM Language l ORDER BY l.name ASC")
    List<Language> findAllIncludingArchived();

    /**
     * Find languages by docker image
     */
    List<Language> findByDockerImage(String dockerImage);

    /**
     * Find all compiled languages (those with compile command)
     */
    @Query("SELECT l FROM Language l WHERE l.compileCmd IS NOT NULL AND l.compileCmd != '' AND l.isArchived = false")
    List<Language> findCompiledLanguages();

    /**
     * Find all interpreted languages (those without compile command)
     */
    @Query("SELECT l FROM Language l WHERE (l.compileCmd IS NULL OR l.compileCmd = '') AND l.isArchived = false")
    List<Language> findInterpretedLanguages();

    /**
     * Check if language is enabled (not archived)
     */
    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM Language l WHERE l.id = :id AND l.isArchived = false")
    boolean isLanguageEnabled(@Param("id") Integer id);

    /**
     * Find languages by partial name match
     */
    @Query("SELECT l FROM Language l WHERE LOWER(l.name) LIKE LOWER(CONCAT('%', :namePart, '%')) AND l.isArchived = false")
    List<Language> findByNameContainingIgnoreCase(@Param("namePart") String namePart);

    /**
     * Count enabled languages
     */
    @Query("SELECT COUNT(l) FROM Language l WHERE l.isArchived = false")
    Long countEnabledLanguages();

    /**
     * Count total languages
     */
    @Query("SELECT COUNT(l) FROM Language l")
    Long countTotalLanguages();

    /**
     * Find project languages (Multi-file program)
     */
    @Query("SELECT l FROM Language l WHERE l.name = 'Multi-file program' AND l.isArchived = false")
    Optional<Language> findProjectLanguage();

    /**
     * Find languages that support specific features
     */
    @Query("SELECT l FROM Language l WHERE l.isArchived = false AND " +
           "(:supportsCompilation IS NULL OR " +
           "(:supportsCompilation = true AND l.compileCmd IS NOT NULL) OR " +
           "(:supportsCompilation = false AND (l.compileCmd IS NULL OR l.compileCmd = '')))")
    List<Language> findLanguagesByFeatures(@Param("supportsCompilation") Boolean supportsCompilation);

    /**
     * Find languages by ID list
     */
    @Query("SELECT l FROM Language l WHERE l.id IN :ids")
    List<Language> findByIdIn(@Param("ids") List<Integer> ids);

    /**
     * Check if language exists and is enabled
     */
    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM Language l " +
           "WHERE l.id = :id AND l.isArchived = false")
    boolean existsByIdAndEnabled(@Param("id") Integer id);

    /**
     * Find languages with custom docker configurations
     */
    @Query("SELECT l FROM Language l WHERE l.dockerImage IS NOT NULL AND l.dockerImage != '' AND l.isArchived = false")
    List<Language> findLanguagesWithCustomDocker();

    /**
     * Get language statistics
     */
    @Query("SELECT l.isArchived, COUNT(l) FROM Language l GROUP BY l.isArchived")
    List<Object[]> getLanguageStatistics();
}
