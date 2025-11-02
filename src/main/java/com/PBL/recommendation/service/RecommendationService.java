package com.PBL.recommendation.service;

import com.PBL.curriculum.Curriculum;
import com.PBL.curriculum.CurriculumRepository;
import com.PBL.enrollment.entity.Enrollment;
import com.PBL.enrollment.entity.LectureProgress;
import com.PBL.enrollment.repository.EnrollmentRepository;
import com.PBL.enrollment.repository.LectureProgressRepository;
import com.PBL.lecture.entity.Lecture;
import com.PBL.lecture.LectureType;
import com.PBL.lecture.repository.LectureRepository;
import com.PBL.recommendation.dto.RecommendationDTOs;
import com.PBL.recommendation.entity.RecommendationLog;
import com.PBL.recommendation.repository.RecommendationLogRepository;
import com.PBL.user.User;
import com.PBL.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 추천 시스템 서비스
 * 개인화 추천 및 유사 문제 추천을 제공
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {

    private final CurriculumRepository curriculumRepository;
    private final LectureRepository lectureRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final LectureProgressRepository lectureProgressRepository;
    private final RecommendationLogRepository recommendationLogRepository;
    private final UserRepository userRepository;

    /**
     * 개인화된 커리큘럼 추천
     * Priority 2
     * 캐싱: 사용자별 추천 결과를 캐싱하여 성능 향상
     */
    @Cacheable(value = "personalizedCurriculums", key = "#userId + ':' + #page + ':' + #size")
    @Transactional(readOnly = true)
    public Map<String, Object> getPersonalizedCurriculums(Long userId, int page, int size) {
        log.info("개인화 추천 요청 - 사용자 ID: {}, 페이지: {}, 크기: {}", userId, page, size);

        // 1. 사용자 수강 이력 분석
        List<Enrollment> enrollments = enrollmentRepository.findByUserIdOrderByEnrolledAtDesc(userId);
        Set<String> userCategories = extractCategoriesFromEnrollments(enrollments);
        Set<String> userTags = extractTagsFromEnrollments(enrollments);
        String preferredDifficulty = extractPreferredDifficulty(enrollments);
        
        // 신규 사용자 여부 확인
        boolean isNewUser = enrollments.isEmpty();
        
        log.debug("사용자 카테고리: {}, 태그: {}, 선호 난이도: {}, 신규 사용자: {}", 
                userCategories, userTags, preferredDifficulty, isNewUser);

        // 2. 모든 공개 커리큘럼 조회 (작성자 포함)
        List<Curriculum> allCurriculums = curriculumRepository.findPublicCurriculumsWithAuthor();

        // 3. 이미 수강 중인 커리큘럼 제외
        Set<Long> enrolledCurriculumIds = enrollments.stream()
                .map(e -> e.getCurriculum().getId())
                .collect(Collectors.toSet());

        // 4. 점수 계산 및 정렬 (병렬 처리 적용)
        List<CurriculumScore> scoredCurriculums = allCurriculums.parallelStream()
                .filter(c -> !enrolledCurriculumIds.contains(c.getId()))
                .map(c -> {
                    BigDecimal score;
                    String reason;
                    if (isNewUser) {
                        score = calculateDefaultCurriculumScore(c);
                        reason = getDefaultRecommendationReason(c);
                    } else {
                        score = calculateCurriculumScore(c, userCategories, userTags, preferredDifficulty);
                        reason = getRecommendationReason(c, userCategories, userTags, preferredDifficulty);
                    }
                    return new CurriculumScore(c, score, reason);
                })
                .filter(sc -> sc.score.compareTo(BigDecimal.ZERO) > 0)
                .sorted((a, b) -> b.score.compareTo(a.score))
                .collect(Collectors.toList());

        // 5. 성능 최적화: 필요한 만큼만 상위 항목 선택 후 페이지네이션
        int maxItemsNeeded = (page + 1) * size * 2; // 필요한 만큼만 확보
        List<CurriculumScore> topCurriculums = scoredCurriculums.stream()
                .limit(Math.min(maxItemsNeeded, scoredCurriculums.size()))
                .collect(Collectors.toList());
        
        // 6. 페이지네이션 적용
        int totalElements = scoredCurriculums.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        int start = page * size;
        int end = Math.min(start + size, topCurriculums.size());
        
        List<CurriculumScore> pagedCurriculums = topCurriculums.subList(Math.min(start, topCurriculums.size()), end);

        // 7. 로그 저장
        logRecommendation(userId, pagedCurriculums, "PERSONALIZED");

        // 8. 응답 생성
        List<RecommendationDTOs.CurriculumRecommendationResponse> responses = pagedCurriculums.stream()
                .map(sc -> RecommendationDTOs.CurriculumRecommendationResponse.from(
                        sc.curriculum, sc.score, sc.reason))
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("curriculums", responses);
        Map<String, Object> meta = new HashMap<>();
        meta.put("currentPage", page);
        meta.put("totalElements", totalElements);
        meta.put("totalPages", totalPages);
        meta.put("hasNext", page < totalPages - 1);
        meta.put("hasPrevious", page > 0);
        result.put("meta", meta);

        return result;
    }

    /**
     * 통합 추천 (커리큘럼 + 강의 혼합)
     * 공개된 커리큘럼과 강의를 점수 기준으로 혼합하여 추천
     * 캐싱: 사용자별 추천 결과를 캐싱하여 성능 향상
     */
    @Cacheable(value = "unifiedRecommendations", key = "#userId + ':' + #page + ':' + #size")
    @Transactional(readOnly = true)
    public Map<String, Object> getUnifiedRecommendations(Long userId, int page, int size) {
        log.info("통합 추천 요청 - 사용자 ID: {}, 페이지: {}, 크기: {}", userId, page, size);

        // 1. 사용자 수강 이력 분석
        List<Enrollment> enrollments = enrollmentRepository.findByUserIdOrderByEnrolledAtDesc(userId);
        Set<String> userCategories = extractCategoriesFromEnrollments(enrollments);
        Set<String> userTags = extractTagsFromEnrollments(enrollments);
        String preferredDifficulty = extractPreferredDifficulty(enrollments);
        
        // 신규 사용자 여부 확인 (수강 이력이 없는 경우)
        boolean isNewUser = enrollments.isEmpty();
        
        log.debug("사용자 카테고리: {}, 태그: {}, 선호 난이도: {}, 신규 사용자: {}", 
                userCategories, userTags, preferredDifficulty, isNewUser);

        // 2. 공개 커리큘럼 추천 (이미 수강한 커리큘럼 제외)
        List<Curriculum> allCurriculums = curriculumRepository.findPublicCurriculumsWithAuthor();
        Set<Long> enrolledCurriculumIds = enrollments.stream()
                .map(e -> e.getCurriculum().getId())
                .collect(Collectors.toSet());
        
        // 병렬 처리로 성능 향상 + 조기 필터링
        List<UnifiedScore> curriculumScores = allCurriculums.parallelStream()
                .filter(c -> !enrolledCurriculumIds.contains(c.getId())) // 이미 수강한 커리큘럼 제외
                .map(c -> {
                    BigDecimal score;
                    String reason;
                    if (isNewUser) {
                        // 신규 사용자는 기본 추천 점수 계산 (파이썬, C, 알고리즘 기초 등)
                        score = calculateDefaultCurriculumScore(c);
                        reason = getDefaultRecommendationReason(c);
                    } else {
                        score = calculateCurriculumScore(c, userCategories, userTags, preferredDifficulty);
                        reason = getRecommendationReason(c, userCategories, userTags, preferredDifficulty);
                    }
                    return new UnifiedScore("CURRICULUM", c.getId(), c, null, score, reason);
                })
                .filter(us -> us.score.compareTo(BigDecimal.ZERO) > 0)
                .collect(Collectors.toList());

        // 3. 공개 강의 추천 (개인화) - 병렬 처리 적용
        List<Lecture> allPublicLectures = lectureRepository.findByIsPublicTrueOrderByCreatedAtDesc();
        Set<Long> excludedLectureIds = getExcludedLectureIds(userId, null);
        List<UnifiedScore> lectureScores = allPublicLectures.parallelStream()
                .filter(l -> !excludedLectureIds.contains(l.getId()))
                .map(l -> {
                    BigDecimal score;
                    String reason;
                    if (isNewUser) {
                        // 신규 사용자는 기본 추천 점수 계산
                        score = calculateDefaultLectureScore(l);
                        reason = getDefaultLectureReason(l);
                    } else {
                        score = calculateLecturePersonalizedScore(l, userCategories, userTags, preferredDifficulty);
                        reason = getLecturePersonalizedReason(l, userCategories, userTags, preferredDifficulty);
                    }
                    return new UnifiedScore("LECTURE", l.getId(), null, l, score, reason);
                })
                .filter(us -> us.score.compareTo(BigDecimal.ZERO) > 0)
                .collect(Collectors.toList());

        // 4. 커리큘럼과 강의를 각각 정렬 (성능 최적화: 전체 정렬 대신 상위만 정렬)
        curriculumScores.sort((a, b) -> b.score.compareTo(a.score));
        lectureScores.sort((a, b) -> b.score.compareTo(a.score));
        
        // 5. 성능 최적화: 필요한 만큼만 상위 항목 선택 (페이지 크기의 3배 정도만 계산)
        int maxItemsNeeded = (page + 1) * size * 3; // 충분한 양 확보
        List<UnifiedScore> topCurriculums = curriculumScores.stream()
                .limit(Math.min(maxItemsNeeded, curriculumScores.size()))
                .collect(Collectors.toList());
        List<UnifiedScore> topLectures = lectureScores.stream()
                .limit(Math.min(maxItemsNeeded, lectureScores.size()))
                .collect(Collectors.toList());
        
        // 6. 커리큘럼과 강의를 교차로 배치 (4개씩 셔플하여 균형잡힌 추천 제공)
        List<UnifiedScore> interleavedScores = interleaveRecommendationsInChunks(topCurriculums, topLectures, 4);
        
        // 7. 페이지네이션 적용
        int totalElements = curriculumScores.size() + lectureScores.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        int start = page * size;
        int end = Math.min(start + size, interleavedScores.size());
        
        List<UnifiedScore> topScores = interleavedScores.subList(Math.min(start, interleavedScores.size()), end);

        log.debug("통합 추천 결과 - 커리큘럼: {}개, 강의: {}개 (전체: 커리큘럼 {}개, 강의 {}개)", 
                topScores.stream().filter(s -> s.type.equals("CURRICULUM")).count(),
                topScores.stream().filter(s -> s.type.equals("LECTURE")).count(),
                curriculumScores.size(),
                lectureScores.size());

        // 6. DTO 변환
        List<RecommendationDTOs.UnifiedRecommendationResponse> responses = topScores.stream()
                .map(us -> {
                    if ("CURRICULUM".equals(us.type)) {
                        Curriculum c = us.curriculum;
                        return RecommendationDTOs.UnifiedRecommendationResponse.builder()
                                .type("CURRICULUM")
                                .id(c.getId())
                                .title(c.getTitle())
                                .description(c.getSummary())
                                .category(c.getCategory())
                                .difficulty(c.getDifficulty())
                                .recommendationScore(us.score)
                                .recommendationReason(us.reason)
                                .tags(c.getTags())
                                .averageRating(c.getAverageRating())
                                .studentCount(c.getStudentCount())
                                .authorName(c.getAuthor() != null ? c.getAuthor().getUsername() : null)
                                .thumbnailImageUrl(c.getThumbnailImageUrl())
                                .build();
                    } else {
                        Lecture l = us.lecture;
                        return RecommendationDTOs.UnifiedRecommendationResponse.builder()
                                .type("LECTURE")
                                .id(l.getId())
                                .title(l.getTitle())
                                .description(l.getDescription())
                                .category(l.getCategory())
                                .difficulty(l.getDifficulty())
                                .recommendationScore(us.score)
                                .recommendationReason(us.reason)
                                .lectureType(l.getType().toString())
                                .build();
                    }
                })
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("recommendations", responses);
        Map<String, Object> meta = new HashMap<>();
        meta.put("currentPage", page);
        meta.put("totalElements", totalElements);
        meta.put("totalPages", totalPages);
        meta.put("hasNext", page < totalPages - 1);
        meta.put("hasPrevious", page > 0);
        result.put("meta", meta);

        return result;
    }

    /**
     * 개인화된 강의 추천 (커리큘럼 추천과 별도)
     * 사용자의 수강 이력과 선호도를 기반으로 강의를 추천합니다.
     */
    @Cacheable(value = "personalizedLectures", key = "#userId + ':' + #page + ':' + #size")
    @Transactional(readOnly = true)
    public Map<String, Object> getPersonalizedLectures(Long userId, int page, int size) {
        log.info("개인화 강의 추천 요청 - 사용자 ID: {}, 페이지: {}, 크기: {}", userId, page, size);

        // 1. 사용자 수강 이력 분석
        List<Enrollment> enrollments = enrollmentRepository.findByUserIdOrderByEnrolledAtDesc(userId);
        Set<String> userCategories = extractCategoriesFromEnrollments(enrollments);
        Set<String> userTags = extractTagsFromEnrollments(enrollments);
        String preferredDifficulty = extractPreferredDifficulty(enrollments);
        
        // 신규 사용자 여부 확인
        boolean isNewUser = enrollments.isEmpty();
        
        log.debug("사용자 카테고리: {}, 태그: {}, 선호 난이도: {}, 신규 사용자: {}", 
                userCategories, userTags, preferredDifficulty, isNewUser);

        // 2. 공개 강의 추천 (개인화) - 병렬 처리 적용
        List<Lecture> allPublicLectures = lectureRepository.findByIsPublicTrueOrderByCreatedAtDesc();
        Set<Long> excludedLectureIds = getExcludedLectureIds(userId, null);
        
        List<LectureScore> scoredLectures = allPublicLectures.parallelStream()
                .filter(l -> !excludedLectureIds.contains(l.getId()))
                .map(l -> {
                    BigDecimal score;
                    String reason;
                    if (isNewUser) {
                        score = calculateDefaultLectureScore(l);
                        reason = getDefaultLectureReason(l);
                    } else {
                        score = calculateLecturePersonalizedScore(l, userCategories, userTags, preferredDifficulty);
                        reason = getLecturePersonalizedReason(l, userCategories, userTags, preferredDifficulty);
                    }
                    return new LectureScore(l, score, reason);
                })
                .filter(sc -> sc.score.compareTo(BigDecimal.ZERO) > 0)
                .sorted((a, b) -> b.score.compareTo(a.score))
                .collect(Collectors.toList());

        // 3. 성능 최적화: 필요한 만큼만 상위 항목 선택 후 페이지네이션
        int maxItemsNeeded = (page + 1) * size * 2; // 필요한 만큼만 확보
        List<LectureScore> topLectures = scoredLectures.stream()
                .limit(Math.min(maxItemsNeeded, scoredLectures.size()))
                .collect(Collectors.toList());

        // 4. 페이지네이션 적용
        int totalElements = scoredLectures.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        int start = page * size;
        int end = Math.min(start + size, topLectures.size());
        
        List<LectureScore> pagedLectures = topLectures.subList(Math.min(start, topLectures.size()), end);

        // 5. 로그 저장
        logLectureRecommendation(userId, pagedLectures, "PERSONALIZED");

        // 6. 응답 생성
        List<RecommendationDTOs.LectureRecommendationResponse> responses = pagedLectures.stream()
                .map(sl -> RecommendationDTOs.LectureRecommendationResponse.from(
                        sl.lecture, sl.score, sl.reason))
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("lectures", responses);
        Map<String, Object> meta = new HashMap<>();
        meta.put("currentPage", page);
        meta.put("totalElements", totalElements);
        meta.put("totalPages", totalPages);
        meta.put("hasNext", page < totalPages - 1);
        meta.put("hasPrevious", page > 0);
        result.put("meta", meta);

        return result;
    }

    /**
     * 개인화 강의 점수 계산
     */
    private BigDecimal calculateLecturePersonalizedScore(Lecture lecture, Set<String> userCategories,
                                                        Set<String> userTags, String preferredDifficulty) {
        BigDecimal score = BigDecimal.ZERO;

        // 1. 카테고리 매칭 (30점)
        if (lecture.getCategory() != null && userCategories.contains(lecture.getCategory())) {
            score = score.add(BigDecimal.valueOf(30));
        }

        // 2. 태그 매칭 (20점)
        Set<String> tags = extractTagsFromLecture(lecture);
        long matchingTags = tags.stream()
                .filter(userTags::contains)
                .count();
        if (matchingTags > 0) {
            score = score.add(BigDecimal.valueOf(Math.min(20, matchingTags * 5)));
        }

        // 3. 난이도 매칭 (20점)
        if (lecture.getDifficulty() != null && Objects.equals(lecture.getDifficulty(), preferredDifficulty)) {
            score = score.add(BigDecimal.valueOf(20));
        }

        // 4. 인기도 (최근 생성된 강의 우선) (30점)
        // 최근 생성된 강의일수록 높은 점수 (단순화: 공개 강의는 모두 인기 있다고 가정)
        score = score.add(BigDecimal.valueOf(30));

        return score;
    }

    /**
     * 개인화 강의 추천 이유 생성
     */
    private String getLecturePersonalizedReason(Lecture lecture, Set<String> userCategories,
                                               Set<String> userTags, String preferredDifficulty) {
        List<String> reasons = new ArrayList<>();

        if (lecture.getCategory() != null && userCategories.contains(lecture.getCategory())) {
            reasons.add("당신이 좋아하는 카테고리");
        }

        Set<String> tags = extractTagsFromLecture(lecture);
        long matchingTags = tags.stream()
                .filter(userTags::contains)
                .count();
        if (matchingTags > 0) {
            reasons.add("관심 있는 주제");
        }

        if (Objects.equals(lecture.getDifficulty(), preferredDifficulty)) {
            reasons.add("적합한 난이도");
        }

        if (reasons.isEmpty()) {
            reasons.add("인기 강의");
        }

        return String.join(", ", reasons);
    }

    /**
     * 유사 문제 강의 추천
     * Priority 1 - 가장 중요한 기능
     */
    public Map<String, Object> getSimilarProblemLectures(
            Long userId, Long lectureId, int page, int size) {
        log.info("유사 문제 추천 요청 - 사용자 ID: {}, 기준 강의 ID: {}, 페이지: {}, 크기: {}", userId, lectureId, page, size);

        // 1. 기준 강의 조회
        Lecture baseLecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new RuntimeException("강의를 찾을 수 없습니다: " + lectureId));

        if (baseLecture.getType() != LectureType.PROBLEM) {
            throw new IllegalArgumentException("해당 강의는 문제 강의가 아닙니다. type: " + baseLecture.getType());
        }

        // 2. 유사 강의 검색 조건
        String category = baseLecture.getCategory();
        String difficulty = baseLecture.getDifficulty();
        Set<String> tags = extractTagsFromLecture(baseLecture);

        log.debug("유사 강의 검색 조건 - 카테고리: {}, 난이도: {}, 태그: {}", category, difficulty, tags);

        // 3. 모든 공개 문제 강의 조회 (더 넓은 범위에서 추천)
        List<Lecture> allPublicProblemLectures = lectureRepository.findByIsPublicTrueAndTypeOrderByCreatedAtDesc(LectureType.PROBLEM);

        // 4. 기준 강의 및 사용자가 이미 학습한 강의 제외
        Set<Long> excludedLectureIds = getExcludedLectureIds(userId, lectureId);

        // 5. 점수 계산 및 정렬 (카테고리/난이도가 다르더라도 점수로 순위 결정) - 병렬 처리 적용
        List<LectureScore> scoredLectures = allPublicProblemLectures.parallelStream()
                .filter(l -> !excludedLectureIds.contains(l.getId()))
                .map(l -> {
                    BigDecimal score = calculateLectureSimilarityScore(l, baseLecture, tags);
                    String reason = getLectureRecommendationReason(l, baseLecture);
                    return new LectureScore(l, score, reason);
                })
                .filter(scored -> scored.score.compareTo(BigDecimal.ZERO) > 0) // 점수가 0보다 큰 것만
                .sorted((a, b) -> b.score.compareTo(a.score))
                .collect(Collectors.toList());

        log.debug("유사 강의 발견: {}개", scoredLectures.size());

        // 6. 성능 최적화: 필요한 만큼만 상위 항목 선택 후 페이지네이션
        int maxItemsNeeded = (page + 1) * size * 2; // 필요한 만큼만 확보
        List<LectureScore> topLectures = scoredLectures.stream()
                .limit(Math.min(maxItemsNeeded, scoredLectures.size()))
                .collect(Collectors.toList());

        // 7. 페이지네이션 적용
        int totalElements = scoredLectures.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        int start = page * size;
        int end = Math.min(start + size, topLectures.size());
        
        List<LectureScore> pagedLectures = topLectures.subList(Math.min(start, topLectures.size()), end);

        // 8. 로그 저장
        logLectureRecommendation(userId, pagedLectures, "SIMILAR_PROBLEM");

        // 9. 응답 생성
        List<RecommendationDTOs.LectureRecommendationResponse> responses = pagedLectures.stream()
                .map(sl -> RecommendationDTOs.LectureRecommendationResponse.from(
                        sl.lecture, sl.score, sl.reason))
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("lectures", responses);
        Map<String, Object> meta = new HashMap<>();
        meta.put("currentPage", page);
        meta.put("totalElements", totalElements);
        meta.put("totalPages", totalPages);
        meta.put("hasNext", page < totalPages - 1);
        meta.put("hasPrevious", page > 0);
        result.put("meta", meta);

        return result;
    }

    // ========== 헬퍼 메서드 ==========

    /**
     * 수강 이력에서 카테고리 추출
     */
    private Set<String> extractCategoriesFromEnrollments(List<Enrollment> enrollments) {
        return enrollments.stream()
                .map(e -> e.getCurriculum().getCategory())
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    /**
     * 수강 이력에서 태그 추출
     */
    private Set<String> extractTagsFromEnrollments(List<Enrollment> enrollments) {
        return enrollments.stream()
                .map(e -> e.getCurriculum().getTags())
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .collect(Collectors.toSet());
    }

    /**
     * 수강 이력에서 선호 난이도 추출
     */
    private String extractPreferredDifficulty(List<Enrollment> enrollments) {
        return enrollments.stream()
                .map(e -> e.getCurriculum().getDifficulty())
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(d -> d, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    /**
     * 강의에서 태그 추출
     */
    private Set<String> extractTagsFromLecture(Lecture lecture) {
        // 강의 엔티티에 tags 필드가 있다면 사용
        // 현재는 빈 Set 반환
        return new HashSet<>();
    }

    /**
     * 커리큘럼 추천 점수 계산
     */
    private BigDecimal calculateCurriculumScore(Curriculum curriculum, Set<String> userCategories,
                                                 Set<String> userTags, String preferredDifficulty) {
        BigDecimal score = BigDecimal.ZERO;

        // 1. 카테고리 매칭 (30점)
        if (curriculum.getCategory() != null && userCategories.contains(curriculum.getCategory())) {
            score = score.add(BigDecimal.valueOf(30));
        }

        // 2. 태그 매칭 (40점)
        if (curriculum.getTags() != null && !curriculum.getTags().isEmpty()) {
            long matchingTags = curriculum.getTags().stream()
                    .filter(userTags::contains)
                    .count();
            if (matchingTags > 0) {
                score = score.add(BigDecimal.valueOf(Math.min(40, matchingTags * 10)));
            }
        }

        // 3. 난이도 매칭 (10점)
        if (curriculum.getDifficulty() != null && Objects.equals(curriculum.getDifficulty(), preferredDifficulty)) {
            score = score.add(BigDecimal.valueOf(10));
        }

        // 4. 평점 점수 (20점)
        if (curriculum.getAverageRating() != null) {
            score = score.add(curriculum.getAverageRating().multiply(BigDecimal.valueOf(4))); // 5.0 -> 20점
        }

        return score;
    }

    /**
     * 강의 유사도 점수 계산
     */
    private BigDecimal calculateLectureSimilarityScore(Lecture lecture, Lecture baseLecture, Set<String> baseTags) {
        BigDecimal score = BigDecimal.ZERO;

        // 1. 카테고리 매칭 (50점) - 가장 중요
        if (Objects.equals(lecture.getCategory(), baseLecture.getCategory())) {
            score = score.add(BigDecimal.valueOf(50));
        }

        // 2. 난이도 매칭 (30점) - 중요
        if (Objects.equals(lecture.getDifficulty(), baseLecture.getDifficulty())) {
            score = score.add(BigDecimal.valueOf(30));
        }

        // 3. 태그 매칭 (20점) - 보조
        Set<String> tags = extractTagsFromLecture(lecture);
        long matchingTags = tags.stream()
                .filter(baseTags::contains)
                .count();
        if (matchingTags > 0) {
            score = score.add(BigDecimal.valueOf(Math.min(20, matchingTags * 5)));
        }

        // 4. 제목 유사도 (10점) - 키워드 매칭
        if (lecture.getTitle() != null && baseLecture.getTitle() != null) {
            String[] baseTitleWords = baseLecture.getTitle().toLowerCase().split("\\s+");
            String[] targetTitleWords = lecture.getTitle().toLowerCase().split("\\s+");
            long matchingWords = Arrays.stream(targetTitleWords)
                    .filter(word -> Arrays.stream(baseTitleWords).anyMatch(baseWord -> baseWord.contains(word) || word.contains(baseWord)))
                    .count();
            if (matchingWords > 0) {
                score = score.add(BigDecimal.valueOf(Math.min(10, matchingWords * 2)));
            }
        }

        return score;
    }

    /**
     * 추천 이유 생성
     */
    private String getRecommendationReason(Curriculum curriculum, Set<String> userCategories,
                                          Set<String> userTags, String preferredDifficulty) {
        List<String> reasons = new ArrayList<>();

        if (curriculum.getCategory() != null && userCategories.contains(curriculum.getCategory())) {
            reasons.add("당신이 좋아하는 카테고리");
        }

        if (curriculum.getTags() != null && !curriculum.getTags().isEmpty()) {
            long matchingTags = curriculum.getTags().stream()
                    .filter(userTags::contains)
                    .count();
            if (matchingTags > 0) {
                reasons.add("관심 있는 주제");
            }
        }

        if (Objects.equals(curriculum.getDifficulty(), preferredDifficulty)) {
            reasons.add("적합한 난이도");
        }

        if (reasons.isEmpty()) {
            reasons.add("인기 강의");
        }

        return String.join(", ", reasons);
    }

    /**
     * 강의 추천 이유 생성
     */
    private String getLectureRecommendationReason(Lecture lecture, Lecture baseLecture) {
        List<String> reasons = new ArrayList<>();

        if (Objects.equals(lecture.getCategory(), baseLecture.getCategory())) {
            reasons.add("같은 카테고리");
        }

        if (Objects.equals(lecture.getDifficulty(), baseLecture.getDifficulty())) {
            reasons.add("같은 난이도");
        }

        return reasons.isEmpty() ? "유사한 문제" : String.join(", ", reasons);
    }

    /**
     * 제외할 강의 ID 목록 조회
     */
    private Set<Long> getExcludedLectureIds(Long userId, Long lectureId) {
        Set<Long> excludedIds = new HashSet<>();
        if (lectureId != null) {
            excludedIds.add(lectureId);
        }

        // 사용자가 이미 학습한 강의들
        List<LectureProgress> progresses = lectureProgressRepository.findByEnrollmentIdIn(
                enrollmentRepository.findByUserIdOrderByEnrolledAtDesc(userId).stream()
                        .map(Enrollment::getId)
                        .collect(Collectors.toList()));

        excludedIds.addAll(progresses.stream()
                .map(p -> p.getLecture().getId())
                .collect(Collectors.toSet()));

        return excludedIds;
    }

    /**
     * 추천 로그 저장
     */
    @Transactional
    private void logRecommendation(Long userId, List<CurriculumScore> scoredCurriculums, String type) {
        User user = userRepository.findById(userId).orElseThrow();
        
        for (int i = 0; i < scoredCurriculums.size(); i++) {
            CurriculumScore cs = scoredCurriculums.get(i);
            RecommendationLog log = RecommendationLog.builder()
                    .user(user)
                    .curriculumId(cs.curriculum.getId())
                    .recommendationType(type)
                    .recommendationScore(cs.score)
                    .displayOrder(i + 1)
                    .build();
            recommendationLogRepository.save(log);
        }
    }

    /**
     * 강의 추천 로그 저장
     */
    @Transactional
    private void logLectureRecommendation(Long userId, List<LectureScore> scoredLectures, String type) {
        User user = userRepository.findById(userId).orElseThrow();
        
        for (int i = 0; i < scoredLectures.size(); i++) {
            LectureScore ls = scoredLectures.get(i);
            RecommendationLog log = RecommendationLog.builder()
                    .user(user)
                    .lectureId(ls.lecture.getId())
                    .recommendationType(type)
                    .recommendationScore(ls.score)
                    .displayOrder(i + 1)
                    .build();
            recommendationLogRepository.save(log);
        }
    }

    // ========== 내부 클래스 ==========

    /**
     * 커리큘럼 점수 래퍼
     */
    private static class CurriculumScore {
        Curriculum curriculum;
        BigDecimal score;
        String reason;

        CurriculumScore(Curriculum curriculum, BigDecimal score, String reason) {
            this.curriculum = curriculum;
            this.score = score;
            this.reason = reason;
        }
    }

    /**
     * 강의 점수 래퍼
     */
    private static class LectureScore {
        Lecture lecture;
        BigDecimal score;
        String reason;

        LectureScore(Lecture lecture, BigDecimal score, String reason) {
            this.lecture = lecture;
            this.score = score;
            this.reason = reason;
        }
    }

    /**
     * 커리큘럼과 강의를 교차로 배치하는 메서드 (4개씩 셔플)
     * 커리큘럼 우선, 4개씩 묶어서 번갈아가며 배치하여 균형잡힌 추천 제공
     */
    private List<UnifiedScore> interleaveRecommendationsInChunks(List<UnifiedScore> curriculums, List<UnifiedScore> lectures, int chunkSize) {
        List<UnifiedScore> result = new ArrayList<>();
        int curriculumIndex = 0;
        int lectureIndex = 0;
        boolean curriculumNext = true; // 다음에 배치할 타입 (true: 커리큘럼, false: 강의)
        
        // 커리큘럼과 강의가 모두 남아있는 동안 교차 배치
        while (curriculumIndex < curriculums.size() || lectureIndex < lectures.size()) {
            if (curriculumNext && curriculumIndex < curriculums.size()) {
                // 커리큘럼을 chunkSize개씩 추가
                for (int i = 0; i < chunkSize && curriculumIndex < curriculums.size(); i++) {
                    result.add(curriculums.get(curriculumIndex++));
                }
                curriculumNext = false; // 다음은 강의
            } else if (!curriculumNext && lectureIndex < lectures.size()) {
                // 강의를 chunkSize개씩 추가
                for (int i = 0; i < chunkSize && lectureIndex < lectures.size(); i++) {
                    result.add(lectures.get(lectureIndex++));
                }
                curriculumNext = true; // 다음은 커리큘럼
            } else {
                // 한쪽이 끝났으면 나머지 추가
                if (curriculumIndex < curriculums.size()) {
                    result.add(curriculums.get(curriculumIndex++));
                } else if (lectureIndex < lectures.size()) {
                    result.add(lectures.get(lectureIndex++));
                } else {
                    break;
                }
            }
        }
        
        return result;
    }

    /**
     * 신규 사용자를 위한 기본 커리큘럼 점수 계산
     * 파이썬, C, 알고리즘 기초 등을 우선 추천
     */
    private BigDecimal calculateDefaultCurriculumScore(Curriculum curriculum) {
        BigDecimal score = BigDecimal.ZERO;
        
        // 기본 점수: 평점 기반 (20점)
        if (curriculum.getAverageRating() != null) {
            score = score.add(curriculum.getAverageRating().multiply(BigDecimal.valueOf(4))); // 5.0 -> 20점
        }
        
        // 기본 점수: 수강생 수 기반 (10점)
        if (curriculum.getStudentCount() != null && curriculum.getStudentCount() > 0) {
            score = score.add(BigDecimal.valueOf(Math.min(10, curriculum.getStudentCount() / 10))); // 100명 -> 10점
        }
        
        // 추천 카테고리/태그 매칭 (70점)
        Set<String> recommendedKeywords = Set.of("파이썬", "python", "C", "알고리즘", "algorithm", "기초", "프로그래밍", "programming");
        
        // 카테고리 매칭 (30점)
        if (curriculum.getCategory() != null) {
            String categoryLower = curriculum.getCategory().toLowerCase();
            if (recommendedKeywords.stream().anyMatch(keyword -> categoryLower.contains(keyword.toLowerCase()))) {
                score = score.add(BigDecimal.valueOf(30));
            }
        }
        
        // 태그 매칭 (40점)
        if (curriculum.getTags() != null && !curriculum.getTags().isEmpty()) {
            long matchingTags = curriculum.getTags().stream()
                    .map(String::toLowerCase)
                    .filter(tag -> recommendedKeywords.stream().anyMatch(keyword -> tag.contains(keyword.toLowerCase())))
                    .count();
            if (matchingTags > 0) {
                score = score.add(BigDecimal.valueOf(Math.min(40, matchingTags * 10)));
            }
        }
        
        // 난이도가 "기초"인 경우 보너스 (20점)
        if ("기초".equals(curriculum.getDifficulty())) {
            score = score.add(BigDecimal.valueOf(20));
        }
        
        return score;
    }

    /**
     * 신규 사용자를 위한 기본 강의 점수 계산
     */
    private BigDecimal calculateDefaultLectureScore(Lecture lecture) {
        BigDecimal score = BigDecimal.valueOf(30); // 기본 인기도 점수
        
        // 추천 카테고리/태그 매칭
        Set<String> recommendedKeywords = Set.of("파이썬", "python", "C", "알고리즘", "algorithm", "기초", "프로그래밍", "programming");
        
        // 카테고리 매칭 (30점)
        if (lecture.getCategory() != null) {
            String categoryLower = lecture.getCategory().toLowerCase();
            if (recommendedKeywords.stream().anyMatch(keyword -> categoryLower.contains(keyword.toLowerCase()))) {
                score = score.add(BigDecimal.valueOf(30));
            }
        }
        
        // 난이도가 "기초"인 경우 보너스 (20점)
        if ("기초".equals(lecture.getDifficulty())) {
            score = score.add(BigDecimal.valueOf(20));
        }
        
        return score;
    }

    /**
     * 신규 사용자를 위한 기본 추천 이유 생성
     */
    private String getDefaultRecommendationReason(Curriculum curriculum) {
        List<String> reasons = new ArrayList<>();
        
        Set<String> recommendedKeywords = Set.of("파이썬", "python", "C", "알고리즘", "algorithm", "기초", "프로그래밍", "programming");
        
        if (curriculum.getCategory() != null) {
            String categoryLower = curriculum.getCategory().toLowerCase();
            if (recommendedKeywords.stream().anyMatch(keyword -> categoryLower.contains(keyword.toLowerCase()))) {
                reasons.add("초보자에게 추천하는 카테고리");
            }
        }
        
        if ("기초".equals(curriculum.getDifficulty())) {
            reasons.add("기초 학습에 적합");
        }
        
        if (curriculum.getAverageRating() != null && curriculum.getAverageRating().compareTo(BigDecimal.valueOf(4.0)) >= 0) {
            reasons.add("높은 평점");
        }
        
        if (curriculum.getStudentCount() != null && curriculum.getStudentCount() >= 50) {
            reasons.add("많은 수강생이 선택한 커리큘럼");
        }
        
        if (reasons.isEmpty()) {
            reasons.add("추천 커리큘럼");
        }
        
        return String.join(", ", reasons);
    }

    /**
     * 신규 사용자를 위한 기본 강의 추천 이유 생성
     */
    private String getDefaultLectureReason(Lecture lecture) {
        List<String> reasons = new ArrayList<>();
        
        Set<String> recommendedKeywords = Set.of("파이썬", "python", "C", "알고리즘", "algorithm", "기초", "프로그래밍", "programming");
        
        if (lecture.getCategory() != null) {
            String categoryLower = lecture.getCategory().toLowerCase();
            if (recommendedKeywords.stream().anyMatch(keyword -> categoryLower.contains(keyword.toLowerCase()))) {
                reasons.add("초보자에게 추천하는 강의");
            }
        }
        
        if ("기초".equals(lecture.getDifficulty())) {
            reasons.add("기초 학습에 적합");
        }
        
        if (reasons.isEmpty()) {
            reasons.add("추천 강의");
        }
        
        return String.join(", ", reasons);
    }

    /**
     * 통합 추천 점수 래퍼 (커리큘럼 + 강의)
     */
    private static class UnifiedScore {
        String type; // "CURRICULUM" or "LECTURE"
        Long id;
        Curriculum curriculum;
        Lecture lecture;
        BigDecimal score;
        String reason;

        UnifiedScore(String type, Long id, Curriculum curriculum, Lecture lecture, BigDecimal score, String reason) {
            this.type = type;
            this.id = id;
            this.curriculum = curriculum;
            this.lecture = lecture;
            this.score = score;
            this.reason = reason;
        }
    }
}

