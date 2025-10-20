package com.PBL.curriculum;

import com.PBL.curriculum.CurriculumDTOs.*;
import com.PBL.lecture.entity.Lecture;
import com.PBL.lecture.repository.LectureRepository;
import com.PBL.user.User;
import com.PBL.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 커리큘럼 서비스
 * 커리큘럼과 강의 연결 관리 비즈니스 로직
 */
@Service
@Transactional
@Slf4j
public class CurriculumService {

    private final CurriculumRepository curriculumRepository;
    private final CurriculumLectureRepository curriculumLectureRepository;
    private final LectureRepository lectureRepository;
    private final UserRepository userRepository;

    @Autowired
    public CurriculumService(
            CurriculumRepository curriculumRepository,
            CurriculumLectureRepository curriculumLectureRepository,
            LectureRepository lectureRepository,
            UserRepository userRepository) {
        this.curriculumRepository = curriculumRepository;
        this.curriculumLectureRepository = curriculumLectureRepository;
        this.lectureRepository = lectureRepository;
        this.userRepository = userRepository;
    }

    // === 커리큘럼 기본 CRUD ===

    /**
     * 모든 커리큘럼 조회 (강의 포함) - DTO 반환
     */
    @Transactional(readOnly = true)
    public List<CurriculumResponse> getAllCurriculums() {
        List<Curriculum> curriculums = curriculumRepository.findAllWithLectures();
        return curriculums.stream()
                .map(CurriculumResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * 공개 커리큘럼만 조회 - DTO 반환
     */
    @Transactional(readOnly = true)
    public List<CurriculumResponse> getPublicCurriculums() {
        List<Curriculum> curriculums = curriculumRepository.findPublicCurriculumsWithLectures();
        log.info("Curriculums found: {}", curriculums.toString());
        return curriculums.stream()
                .map(CurriculumResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * ID로 커리큘럼 상세 조회
     */
    @Transactional(readOnly = true)
    public Optional<Curriculum> getCurriculumById(Long id) {
        return curriculumRepository.findByIdWithLectures(id);
    }

    /**
     * ID로 커리큘럼 상세 조회 (권한 체크 포함)
     * 공개 커리큘럼은 누구나, 비공개 커리큘럼은 작성자만 조회 가능
     */
    @Transactional(readOnly = true)
    public Optional<Curriculum> getCurriculumByIdWithPermission(Long id, Long userId) {
        Optional<Curriculum> curriculumOpt = curriculumRepository.findByIdWithLectures(id);
        if (curriculumOpt.isEmpty()) {
            return Optional.empty();
        }

        Curriculum curriculum = curriculumOpt.get();
        // 공개 커리큘럼이거나 작성자인 경우
        if (curriculum.isPublicCurriculum() || curriculum.isAuthor(userId)) {
            return curriculumOpt;
        }

        return Optional.empty();
    }

    /**
     * ID로 커리큘럼 상세 조회 - 권한 체크 + DTO 반환
     * 공개 커리큘럼은 누구나, 비공개 커리큘럼은 작성자만 조회 가능
     */
    @Transactional(readOnly = true)
    public CurriculumDetailResponse getCurriculumByIdWithAuth(Long id, Long userId) {
        Optional<Curriculum> curriculumOpt = curriculumRepository.findByIdWithLectures(id);

        if (curriculumOpt.isEmpty()) {
            throw new RuntimeException("커리큘럼을 찾을 수 없습니다: " + id);
        }

        Curriculum curriculum = curriculumOpt.get();

        // 권한 체크: 공개 커리큘럼이거나 작성자인 경우만 조회 가능
        if (!curriculum.isPublicCurriculum() && (userId == null || !curriculum.isAuthor(userId))) {
            throw new SecurityException("이 커리큘럼에 접근할 권한이 없습니다.");
        }

        return new CurriculumDetailResponse(curriculum);
    }

    /**
     * 커리큘럼 생성
     */
    public Curriculum createCurriculum(String title, String description, boolean isPublic) {
        Curriculum curriculum = new Curriculum(title, description);
        curriculum.setIsPublic(isPublic);
        return curriculumRepository.save(curriculum);
    }

    /**
     * 커리큘럼 생성 (작성자 포함)
     */
    public Curriculum createCurriculum(String title, String description, boolean isPublic, User author) {
        Curriculum curriculum = new Curriculum(title, description);
        curriculum.setIsPublic(isPublic);
        curriculum.setAuthor(author);
        return curriculumRepository.save(curriculum);
    }

    /**
     * 커리큘럼 생성 (작성자 및 메타데이터 포함)
     */
    @Transactional
    public CurriculumResponse createCurriculum(CreateCurriculumRequest request, User author) {
        Curriculum curriculum = new Curriculum(request.getTitle(), request.getDescription());
        curriculum.setIsPublic(request.isPublic());
        curriculum.setAuthor(author);
        curriculum.setDifficulty(request.getDifficulty());
        curriculum.setSummary(request.getSummary());
        curriculum.setTags(request.getTags());
        curriculum.setThumbnailImageUrl(request.getThumbnailImageUrl());
        curriculum.setDurationMinutes(request.getDurationMinutes());
        curriculum.setCategory(request.getCategory());
        curriculum = curriculumRepository.save(curriculum);
        return new CurriculumResponse(curriculum);
    }

    /**
     * 커리큘럼 생성 - 사용자 인증 + DTO 반환
     */
    @Transactional
    public CurriculumResponse createCurriculumWithAuth(CreateCurriculumRequest request, Long userId) {
        // 사용자 권한 확인
        if (userId == null) {
            throw new SecurityException("사용자 인증이 필요합니다.");
        }

        // 작성자 정보 확인
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));

        return createCurriculum(request, author);
    }

    /**
     * 커리큘럼 수정
     */
    @Transactional
    public Curriculum updateCurriculum(Long id, UpdateCurriculumRequest request) {
        Curriculum curriculum = curriculumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("커리큘럼을 찾을 수 없습니다: " + id));

        // 기본 필드 업데이트 (null이 아닐 때만)
        if (request.getTitle() != null) {
            curriculum.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            curriculum.setDescription(request.getDescription());
        }
        if (request.getIsPublic() != null) {
            curriculum.setIsPublic(request.getIsPublic());
        }
        if (request.getDifficulty() != null) {
            curriculum.setDifficulty(request.getDifficulty());
        }
        if (request.getSummary() != null) {
            curriculum.setSummary(request.getSummary());
        }
        if (request.getTags() != null) {
            curriculum.setTags(request.getTags());
        }
        if (request.getThumbnailImageUrl() != null) {
            curriculum.setThumbnailImageUrl(request.getThumbnailImageUrl());
        }
        if (request.getDurationMinutes() != null) {
            curriculum.setDurationMinutes(request.getDurationMinutes());
        }
        if (request.getCategory() != null) {
            curriculum.setCategory(request.getCategory());
        }

        return curriculumRepository.save(curriculum);
    }

    /**
     * 커리큘럼 수정 - 권한 체크 + DTO 반환
     */
    @Transactional
    public CurriculumResponse updateCurriculumWithAuth(Long id, UpdateCurriculumRequest request, Long userId) {
        // 사용자 권한 확인
        if (userId == null) {
            throw new SecurityException("사용자 인증이 필요합니다.");
        }

        // 커리큘럼 존재 여부 먼저 확인
        Optional<Curriculum> curriculumOpt = curriculumRepository.findById(id);
        if (curriculumOpt.isEmpty()) {
            throw new RuntimeException("커리큘럼을 찾을 수 없습니다: " + id);
        }

        // 작성자 권한 확인
        if (!canEditCurriculum(id, userId)) {
            throw new SecurityException("이 커리큘럼을 수정할 권한이 없습니다.");
        }

        Curriculum curriculum = updateCurriculum(id, request);
        return new CurriculumResponse(curriculum);
    }

    /**
     * 커리큘럼 삭제
     */
    @Transactional
    public void deleteCurriculum(Long id) {
        if (!curriculumRepository.existsById(id)) {
            throw new RuntimeException("커리큘럼을 찾을 수 없습니다: " + id);
        }
        curriculumRepository.deleteById(id);
    }

    /**
     * 커리큘럼 삭제 - 권한 체크 통합
     */
    @Transactional
    public void deleteCurriculumWithAuth(Long id, Long userId) {
        // 사용자 권한 확인
        if (userId == null) {
            throw new SecurityException("사용자 인증이 필요합니다.");
        }

        // 커리큘럼 존재 여부 먼저 확인
        Optional<Curriculum> curriculumOpt = curriculumRepository.findById(id);
        if (curriculumOpt.isEmpty()) {
            throw new RuntimeException("커리큘럼을 찾을 수 없습니다: " + id);
        }

        // 작성자 권한 확인
        if (!canDeleteCurriculum(id, userId)) {
            throw new SecurityException("이 커리큘럼을 삭제할 권한이 없습니다.");
        }

        deleteCurriculum(id);
    }

    // === 강의 연결 관리 ===

    /**
     * 커리큘럼에 강의 추가
     */
    @Transactional
    public void addLectureToCurriculum(Long curriculumId, Long lectureId, boolean isRequired,
                                      String originalAuthor, String sourceInfo) {
        // 커리큘럼 존재 확인
        Curriculum curriculum = curriculumRepository.findById(curriculumId)
                .orElseThrow(() -> new RuntimeException("커리큘럼을 찾을 수 없습니다: " + curriculumId));

        // 강의 존재 확인
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new RuntimeException("강의를 찾을 수 없습니다: " + lectureId));

        // 이미 연결되어 있는지 확인
        if (curriculum.containsLecture(lectureId)) {
            throw new RuntimeException("이미 커리큘럼에 포함된 강의입니다.");
        }

        // 공개 강의가 아닌 경우 원작자 정보 확인
        if (!lecture.isPublicLecture() && originalAuthor != null) {
            throw new RuntimeException("비공개 강의는 다른 사용자가 링크할 수 없습니다.");
        }

        // 강의 추가
        curriculum.addLecture(lectureId, isRequired, originalAuthor, sourceInfo);
        curriculumRepository.save(curriculum);
    }

    /**
     * 커리큘럼에서 강의 제거
     */
    @Transactional
    public void removeLectureFromCurriculum(Long curriculumId, Long lectureId) {
        Curriculum curriculum = curriculumRepository.findById(curriculumId)
                .orElseThrow(() -> new RuntimeException("커리큘럼을 찾을 수 없습니다: " + curriculumId));

        if (!curriculum.containsLecture(lectureId)) {
            throw new RuntimeException("커리큘럼에 포함되지 않은 강의입니다.");
        }

        curriculum.removeLecture(lectureId);
        curriculumRepository.save(curriculum);
    }

    /**
     * 커리큘럼 내 강의 순서 변경
     */
    @Transactional
    public void reorderLecturesInCurriculum(Long curriculumId, List<Long> lectureIds) {
        Curriculum curriculum = curriculumRepository.findByIdWithLectures(curriculumId)
                .orElseThrow(() -> new RuntimeException("커리큘럼을 찾을 수 없습니다: " + curriculumId));

        List<CurriculumLecture> lectures = curriculum.getLectures();

        // 순서 재정렬
        for (int i = 0; i < lectureIds.size(); i++) {
            Long lectureId = lectureIds.get(i);
            CurriculumLecture curriculumLecture = lectures.stream()
                    .filter(cl -> cl.getLectureId().equals(lectureId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("커리큘럼에 포함되지 않은 강의입니다: " + lectureId));

            curriculumLecture.setOrderIndex(i + 1);
        }

        curriculumRepository.save(curriculum);
    }

    // === 공개 강의 조회 (커리큘럼 생성 시 사용) ===

    /**
     * 모든 공개 강의 조회 - DTO 반환
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getPublicLectures() {
        List<Lecture> lectures = lectureRepository.findByIsPublicTrueOrderByCreatedAtDesc();
        return lectures.stream()
                .map(this::toLectureMap)
                .collect(Collectors.toList());
    }

    /**
     * 공개 강의 검색 - DTO 반환
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> searchPublicLectures(String title, String category, String difficulty, String type) {
        com.PBL.lecture.LectureType lectureType = null;
        if (type != null && !type.trim().isEmpty()) {
            try {
                lectureType = com.PBL.lecture.LectureType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                // 잘못된 타입이면 null로 처리 (모든 타입 검색)
                lectureType = null;
            }
        }

        List<Lecture> lectures = lectureRepository.findPublicLecturesBySearchCriteria(
                title,
                category,
                difficulty,
                lectureType,
                lectureType != null ? lectureType.name() : null
        );
        return lectures.stream()
                .map(this::toLectureMap)
                .collect(Collectors.toList());
    }

    /**
     * Lecture 엔티티를 안전한 Map으로 변환 (Lazy Loading 방지)
     */
    private Map<String, Object> toLectureMap(Lecture lecture) {
        log.info(lecture.toString());
        Map<String, Object> map = new HashMap<>();
        map.put("id", lecture.getId());
        map.put("title", lecture.getTitle());
        map.put("description", lecture.getDescription());
        map.put("type", lecture.getType());
        map.put("category", lecture.getCategory());
        map.put("difficulty", lecture.getDifficulty());
        map.put("isPublic", lecture.getIsPublic());
        map.put("createdAt", lecture.getCreatedAt());
        map.put("updatedAt", lecture.getUpdatedAt());

        // 태그 추가
        map.put("tags", lecture.getTags() != null ? lecture.getTags() : new ArrayList<>());

        // 썸네일 이미지 URL 추가
        map.put("thumbnailImageUrl", lecture.getThumbnailImageUrl());

        // 소요시간 추가
        map.put("durationMinutes", lecture.getDurationMinutes());

        // 테스트케이스는 개수만 포함 (Lazy Loading 방지)
        try {
            map.put("testCaseCount", lecture.getTestCases() != null ? lecture.getTestCases().size() : 0);
        } catch (Exception e) {
            map.put("testCaseCount", 0);
        }

        return map;
    }

    // === 커리큘럼 검색 ===

    /**
     * 커리큘럼 제목으로 검색 (강의 포함) - DTO 반환
     */
    @Transactional(readOnly = true)
    public List<CurriculumResponse> searchCurriculums(String title) {
        return curriculumRepository.findByTitleContainingIgnoreCaseWithLectures(title).stream()
                .map(CurriculumResponse::new)
                .toList();
    }

    /**
     * 공개 커리큘럼 제목으로 검색 (강의 포함) - DTO 반환
     */
    @Transactional(readOnly = true)
    public List<CurriculumResponse> searchPublicCurriculums(String title) {
        return curriculumRepository.findPublicByTitleContainingIgnoreCaseWithLectures(title).stream()
                .map(CurriculumResponse::new)
                .toList();
    }

    // === 강의 삭제 시 자동 정리 ===

    /**
     * 강의가 삭제될 때 모든 커리큘럼에서 해당 강의 제거
     * (LectureService에서 호출됨)
     */
    @Transactional
    public void removeLectureFromAllCurriculums(Long lectureId) {
        curriculumLectureRepository.deleteByLectureId(lectureId);
    }

    // === 커리큘럼 공개/비공개 설정 ===

    /**
     * 커리큘럼 공개
     */
    @Transactional
    public void publishCurriculum(Long id) {
        Curriculum curriculum = curriculumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("커리큘럼을 찾을 수 없습니다: " + id));

        curriculum.publish();
        curriculumRepository.save(curriculum);
    }

    /**
     * 커리큘럼 비공개
     */
    @Transactional
    public void unpublishCurriculum(Long id) {
        Curriculum curriculum = curriculumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("커리큘럼을 찾을 수 없습니다: " + id));

        curriculum.unpublish();
        curriculumRepository.save(curriculum);
    }

    // === 권한 체크 메서드 ===

    /**
     * 커리큘럼 조회 권한 체크
     * 공개 커리큘럼은 누구나, 비공개 커리큘럼은 작성자만
     */
    @Transactional(readOnly = true)
    public boolean canViewCurriculum(Long curriculumId, Long userId) {
        Optional<Curriculum> curriculumOpt = curriculumRepository.findById(curriculumId);
        if (curriculumOpt.isEmpty()) {
            return false;
        }

        Curriculum curriculum = curriculumOpt.get();
        // 공개 커리큘럼이거나 작성자인 경우
        return curriculum.isPublicCurriculum() || curriculum.isAuthor(userId);
    }

    /**
     * 커리큘럼 수정 권한 체크
     * 작성자만 수정 가능
     */
    @Transactional(readOnly = true)
    public boolean canEditCurriculum(Long curriculumId, Long userId) {
        Optional<Curriculum> curriculumOpt = curriculumRepository.findById(curriculumId);
        if (curriculumOpt.isEmpty()) {
            return false;
        }

        Curriculum curriculum = curriculumOpt.get();
        return curriculum.isAuthor(userId);
    }

    /**
     * 커리큘럼 삭제 권한 체크
     * 작성자만 삭제 가능
     */
    @Transactional(readOnly = true)
    public boolean canDeleteCurriculum(Long curriculumId, Long userId) {
        return canEditCurriculum(curriculumId, userId);
    }

    /**
     * 사용자의 커리큘럼 목록 조회 (강의 포함) - DTO 반환
     */
    @Transactional(readOnly = true)
    public List<CurriculumResponse> getUserCurriculums(Long userId) {
        List<Curriculum> curriculums = curriculumRepository.findByAuthorIdWithLectures(userId);
        return curriculums.stream()
                .map(CurriculumResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * 사용자의 공개 커리큘럼 목록 조회 (강의 포함) - DTO 반환
     */
    @Transactional(readOnly = true)
    public List<CurriculumResponse> getUserPublicCurriculums(Long userId) {
        List<Curriculum> curriculums = curriculumRepository.findByAuthorIdAndIsPublicWithLectures(userId, true);
        return curriculums.stream()
                .map(CurriculumResponse::new)
                .collect(Collectors.toList());
    }

    // === 수강생 수 관리 ===

    /**
     * 커리큘럼의 수강생 수 증가 (Atomic operation)
     */
    @Transactional
    public void incrementStudentCount(Long curriculumId) {
        int updated = curriculumRepository.incrementStudentCountAtomic(curriculumId);
        if (updated == 0) {
            throw new RuntimeException("커리큘럼을 찾을 수 없습니다: " + curriculumId);
        }
    }

    /**
     * 커리큘럼의 수강생 수 감소 (Atomic operation)
     */
    @Transactional
    public void decrementStudentCount(Long curriculumId) {
        int updated = curriculumRepository.decrementStudentCountAtomic(curriculumId);
        if (updated == 0) {
            // 커리큘럼이 없거나 이미 0인 경우
            if (!curriculumRepository.existsById(curriculumId)) {
                throw new RuntimeException("커리큘럼을 찾을 수 없습니다: " + curriculumId);
            }
            // 이미 0인 경우는 조용히 무시
        }
    }

    /**
     * 커리큘럼의 평균 별점 업데이트
     */
    @Transactional
    public void updateAverageRating(Long curriculumId, BigDecimal newRating) {
        Curriculum curriculum = curriculumRepository.findById(curriculumId)
                .orElseThrow(() -> new RuntimeException("커리큘럼을 찾을 수 없습니다: " + curriculumId));

        curriculum.setAverageRating(newRating);
        curriculumRepository.save(curriculum);
    }

    /*
     * 커리큘럼 다음 페이지 조회
     */
    @Transactional
    public CurriculumDTOs.CurriculumNextLecture navigationLectures(Long curriculumId , Long currentLectureId) {
        // 1. 현재 orderIndex 조회
        Integer currentOrder = curriculumLectureRepository
                .findOrderIndexByCurriculumIdAndLectureId(curriculumId, currentLectureId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "커리큘럼에서 해당 강의를 찾을 수 없습니다."
                ));

        // 2. 다음/이전 강의 조회
        Long nextLectureId = curriculumLectureRepository
                .findNextLectureId(curriculumId, currentOrder)
                .orElse(null);

        Long preLectureId = curriculumLectureRepository
                .findPreviousLectureId(curriculumId, currentOrder)
                .orElse(null);

        // 3. DTO 반환
        return CurriculumDTOs.CurriculumNextLecture.builder()
                .curriculumId(curriculumId)
                .currentLectureId(currentLectureId)
                .nextLectureId(nextLectureId)
                .preLectureId(preLectureId)
                .build();
    }
}