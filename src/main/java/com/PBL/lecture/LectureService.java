package com.PBL.lecture;
import com.PBL.lab.core.entity.Constraints;
import com.PBL.lecture.dto.CreateLectureRequest;
import com.PBL.lecture.dto.LectureResponse;
import com.PBL.lecture.dto.TestCaseRequest;
import com.PBL.lecture.entity.Lecture;
import com.PBL.lecture.entity.TestCase;
import com.PBL.lecture.repository.LectureRepository;
import com.PBL.user.User;
import lombok.extern.slf4j.Slf4j;

import org.checkerframework.checker.units.qual.t;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 강의 관리 서비스
 * 강의의 생성, 조회, 수정, 삭제 및 비즈니스 로직 처리
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class LectureService {

    private final LectureRepository lectureRepository;
    
    public LectureService(LectureRepository lectureRepository) {
        this.lectureRepository = lectureRepository;
    }

    // === 기본 CRUD ===

    /**
     * 강의 생성 (CreateLectureRequest 사용)
     */
    @Transactional
    public Lecture createLecture(CreateLectureRequest createLectureRequest, User author) {
        Lecture lecture = Lecture.from(createLectureRequest,author);
        return lectureRepository.save(lecture);
    }

    /**
     * AI ToolService용 Entity 조회 (기본)
     */
    @Transactional(readOnly = true)
    public Optional<Lecture> getLecture(Long id) {
        return lectureRepository.findById(id);
    }

    /**
     * AI ToolService용 Entity 조회 (테스트케이스 포함)
     */
    @Transactional(readOnly = true)
    public Optional<Lecture> getLectureWithTestCases(Long id) {
        return lectureRepository.findByIdWithTestCases(id);
    }

    /**
     * 강의 Entity 조회 (Enrollment용)
     * getLecture(id)의 별칭 메소드
     */
    public Optional<Lecture> getLectureById(Long id) {
        return getLecture(id);
    }

    /**
     * 강의 Entity 조회 (Controller, AI Tool용)
     * getLecture(id)의 별칭 메소드
     */
    public Optional<Lecture> findLectureEntity(Long id) {
        return getLecture(id);
    }

    /**
     * 강의 수정 (권한 체크 포함)
     * - 부분 업데이트 지원 (null이 아닌 필드만 업데이트)
     * - Constraints, TestCase 포함 전체 업데이트
     */
    @Transactional
    public Lecture updateLecture(Long id, CreateLectureRequest request, Long userId) {
        Lecture existingLecture = lectureRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다: " + id));

        // 권한 체크: 작성자만 수정 가능
        if (!existingLecture.isAuthor(userId)) {
            throw new AccessDeniedException("이 강의를 수정할 권한이 없습니다.");
        }

        // 기본 필드 업데이트 (null이 아닐 때만)
        if (request.getTitle() != null) {
            existingLecture.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            existingLecture.setDescription(request.getDescription());
        }
        if (request.getType() != null) {
            existingLecture.setType(request.getType());
        }
        if (request.getDifficulty() != null) {
            existingLecture.setDifficulty(request.getDifficulty());
        }
        if (request.getCategory() != null) {
            existingLecture.setCategory(request.getCategory());
        }
        if (request.getIsPublic() != null) {
            existingLecture.setIsPublic(request.getIsPublic());
        }

        // 새로 추가된 필드 업데이트
        if (request.getTags() != null) {
            existingLecture.setTags(request.getTags());
        }
        if (request.getThumbnailImageUrl() != null) {
            existingLecture.setThumbnailImageUrl(request.getThumbnailImageUrl());
        }
        if (request.getDurationMinutes() > 0) {
            existingLecture.setDurationMinutes(request.getDurationMinutes());
        }
        if (request.getContent() != null) {
            existingLecture.setContent(request.getContent());
        }

        // Constraints 업데이트
        if (request.getConstraints() != null) {
            Constraints constraints = Constraints.build(request.getConstraints());
            existingLecture.setConstraints(constraints);
        }

        // TestCase 업데이트 (기존 테스트케이스 삭제 후 새로 추가)
        if (request.getTestCases() != null) {
            // 기존 테스트케이스 제거
            existingLecture.getTestCases().clear();

            // 새 테스트케이스 추가 (양방향 관계 설정)
            List<TestCase> newTestCases = request.getTestCases().stream()
                    .map(dto -> TestCase.builder()
                            .input(dto.getInput())
                            .expectedOutput(dto.getExpectedOutput())
                            .lecture(existingLecture)  // 양방향 관계 설정
                            .build())
                    .toList();
            existingLecture.getTestCases().addAll(newTestCases);
        }

        validateLecture(existingLecture);
        return lectureRepository.save(existingLecture);
    }

    /**
     * 강의 삭제
     */
    @Transactional
    public void deleteLecture(Long id) {
        if (!lectureRepository.existsById(id)) {
            throw new IllegalArgumentException("강의를 찾을 수 없습니다: " + id);
        }
        lectureRepository.deleteById(id);
    }

    // === 검색 및 필터링 ===

    /**
     * AI ToolService용 카테고리별 강의 Entity 조회
     */
    @Transactional(readOnly = true)
    public List<Lecture> findLectureEntitiesByCategory(String category) {
        return lectureRepository.findByCategory(category);
    }

    /**
     * AI ToolService용 제목 검색 Entity 조회
     */
    @Transactional(readOnly = true)
    public List<Lecture> findLectureEntitiesByTitle(String title) {
        return lectureRepository.findByTitleContainingIgnoreCase(title);
    }

    /**
     * 복합 검색 (페이징, DTO 반환)
     * 트랜잭션 내부에서 검색 및 변환 수행
     */
    @Transactional(readOnly = true)
    public Map<String, Object> searchLectures(String title, String category, String difficulty,
                                        LectureType type, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        String typeStr = type != null ? type.name() : null;
        Page<Lecture> lecturesPage = lectureRepository.findBySearchCriteria(title, category, difficulty, type, typeStr, pageable);

        // 트랜잭션 내부에서 DTO 변환
        List<LectureResponse> responses = lecturesPage.getContent().stream()
                .map(LectureResponse::from)
                .toList();

        return Map.of(
                "lectures", responses,
                "currentPage", lecturesPage.getNumber(),
                "totalElements", lecturesPage.getTotalElements(),
                "totalPages", lecturesPage.getTotalPages(),
                "hasNext", lecturesPage.hasNext(),
                "hasPrevious", lecturesPage.hasPrevious()
        );
    }

    /**
     * AI ToolService용 타입별 강의 Entity 조회
     */
    @Transactional(readOnly = true)
    public List<Lecture> findLectureEntitiesByType(LectureType type) {
        return lectureRepository.findByType(type);
    }

    /**
     * 타입별 강의 조회 (DTO 반환)
     * 트랜잭션 내부에서 조회 및 변환 수행
     */
    @Transactional(readOnly = true)
    public List<LectureResponse> getLecturesByType(LectureType type) {
        List<Lecture> lectures = lectureRepository.findByType(type);
        // 트랜잭션 내부에서 DTO 변환
        return lectures.stream()
                .map(LectureResponse::from)
                .toList();
    }

    /**
     * AI ToolService용 최근 강의 Entity 조회
     */
    @Transactional(readOnly = true)
    public List<Lecture> findRecentLectureEntities() {
        return lectureRepository.findTop10ByOrderByCreatedAtDesc();
    }

    /**
     * 최근 강의 조회 (DTO 반환)
     * 트랜잭션 내부에서 조회 및 변환 수행
     */
    @Transactional(readOnly = true)
    public List<LectureResponse> getRecentLectures(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<Lecture> lectures = lectureRepository.findAll(pageable).getContent();
        // 트랜잭션 내부에서 DTO 변환
        return lectures.stream()
                .map(LectureResponse::from)
                .toList();
    }

    // === 테스트케이스 관리 ===

    /**
     * 테스트케이스 추가 (DTO 반환, 권한 체크 포함)
     * 트랜잭션 내부에서 권한 체크, 추가 및 변환 수행
     */
    @Transactional
    public LectureResponse addTestCase(Long lectureId, TestCaseRequest testCaseRequest, Long userId) {
        Lecture lecture = lectureRepository.findByIdWithTestCases(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다: " + lectureId));

        // 권한 체크: 작성자만 테스트케이스 추가 가능
        if (!lecture.isAuthor(userId)) {
            throw new AccessDeniedException("이 강의를 수정할 권한이 없습니다.");
        }

        if (!lecture.isProblemType()) {
            throw new IllegalArgumentException("문제 타입 강의에만 테스트케이스를 추가할 수 있습니다.");
        }

        lecture.addTestCase(testCaseRequest.getInput(), testCaseRequest.getExpectedOutput());
        lecture = lectureRepository.save(lecture);
        // 트랜잭션 내부에서 DTO 변환
        return LectureResponse.from(lecture);
    }

    /**
     * 모든 테스트케이스 제거 (DTO 반환, 권한 체크 포함)
     * 트랜잭션 내부에서 권한 체크, 삭제 및 변환 수행
     */
    @Transactional
    public LectureResponse clearTestCases(Long lectureId, Long userId) {
        Lecture lecture = lectureRepository.findByIdWithTestCases(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다: " + lectureId));

        // 권한 체크: 작성자만 테스트케이스 삭제 가능
        if (!lecture.isAuthor(userId)) {
            throw new AccessDeniedException("이 강의를 수정할 권한이 없습니다.");
        }

        lecture.clearTestCases();
        lecture = lectureRepository.save(lecture);
        // 트랜잭션 내부에서 DTO 변환
        return LectureResponse.from(lecture);
    }

    // === 통계 및 유틸리티 ===

    /**
     * 강의 통계 조회
     */

    public List<Object[]> getLectureStatsByType() {
        return lectureRepository.countByType();
    }

    /**
     * 카테고리 목록 조회
     */
    public List<Object[]> getCategoryStats() {
        return lectureRepository.countByCategory();
    }

    /**
     * 테스트케이스가 있는 문제 강의만 조회
     */
    public List<Lecture> getProblemLecturesWithTestCases() {
        return lectureRepository.findProblemLecturesWithTestCases(LectureType.PROBLEM);
    }

    // === 공개/비공개 설정 ===

    /**
     * 강의 공개
     */
    @Transactional
    public void publishLecture(Long id) {
        Lecture lecture = lectureRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다: " + id));

        lecture.makePublic();
        lectureRepository.save(lecture);
    }

    /**
     * 강의 비공개
     */
    @Transactional
    public void unpublishLecture(Long id) {
        Lecture lecture = lectureRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다: " + id));

        lecture.makePrivate();
        lectureRepository.save(lecture);
    }

    /**
     * AI ToolService용 공개 강의 Entity 조회
     */
    public List<Lecture> findPublicLectureEntities() {
        return lectureRepository.findByIsPublicTrueOrderByCreatedAtDesc();
    }

    /**
     * AI ToolService용 공개 강의 검색 Entity 조회 (검색 조건)
     */
    public List<Lecture> findPublicLectureEntitiesBySearch(String title, String category, String difficulty, LectureType type) {
        return lectureRepository.findPublicLecturesBySearchCriteria(title, category, difficulty, type,
                type != null ? type.name() : null);
    }

    /**
     * 모든 공개 강의 조회 (페이징, DTO 반환)
     * 트랜잭션 내부에서 조회 및 변환 수행
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getPublicLectures(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));
        Page<Lecture> lecturesPage = lectureRepository.findByIsPublicTrue(pageable);

        // 트랜잭션 내부에서 DTO 변환
        List<LectureResponse> responses = lecturesPage.getContent().stream()
                .map(LectureResponse::from)
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("lectures", responses);
        response.put("meta", createPaginationMeta(lecturesPage));
        return response;
    }

    /**
     * 공개 강의 검색 (페이징, DTO 반환)
     * 트랜잭션 내부에서 검색 및 변환 수행
     */
    @Transactional(readOnly = true)
    public Map<String, Object> searchPublicLectures(String title, String category, String difficulty, LectureType type, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        String typeStr = type != null ? type.name() : null;
        Page<Lecture> lecturesPage = lectureRepository.findPublicLecturesBySearchCriteria(title, category, difficulty, type, typeStr, pageable);

        // 트랜잭션 내부에서 DTO 변환
        List<LectureResponse> responses = lecturesPage.getContent().stream()
                .map(LectureResponse::from)
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("lectures", responses);
        response.put("meta", createPaginationMeta(lecturesPage));
        return response;
    }

    // === 권한 체크 메서드 ===

    /**
     * 강의 조회 권한 체크
     * 공개 강의는 누구나, 비공개 강의는 작성자만
     */
    public boolean canViewLecture(Long lectureId, Long userId) {
        Optional<Lecture> lectureOpt = lectureRepository.findById(lectureId);
        if (lectureOpt.isEmpty()) {
            return false;
        }
        
        Lecture lecture = lectureOpt.get();
        // 공개 강의이거나 작성자인 경우
        return lecture.isPublicLecture() || lecture.isAuthor(userId);
    }

    /**
     * 강의 수정 권한 체크
     * 작성자만 수정 가능
     */
    public boolean canEditLecture(Long lectureId, Long userId) {
        Optional<Lecture> lectureOpt = lectureRepository.findById(lectureId);
        if (lectureOpt.isEmpty()) {
            return false;
        }
        
        Lecture lecture = lectureOpt.get();
        return lecture.isAuthor(userId);
    }

    /**
     * 강의 삭제 권한 체크
     * 작성자만 삭제 가능
     */
    public boolean canDeleteLecture(Long lectureId, Long userId) {
        return canEditLecture(lectureId, userId);
    }

    /**
     * 사용자의 강의 목록 조회 (DTO 반환)
     * 트랜잭션 내부에서 조회 및 변환 수행
     */
    @Transactional(readOnly = true)
    public List<LectureResponse> getUserLectures(Long userId) {
        List<Lecture> lectures = lectureRepository.findByAuthorId(userId);
        // 트랜잭션 내부에서 DTO 변환
        return lectures.stream()
                .map(LectureResponse::from)
                .toList();
    }

    /**
     * 사용자의 강의 목록 조회 (페이징, DTO 반환)
     * 트랜잭션 내부에서 조회 및 변환 수행
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getUserLectures(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Lecture> lecturesPage = lectureRepository.findByAuthorId(userId, pageable);

        // 트랜잭션 내부에서 DTO 변환
        List<LectureResponse> responses = lecturesPage.getContent().stream()
                .map(LectureResponse::from)
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("lectures", responses);
        response.put("meta", createPaginationMeta(lecturesPage));
        return response;
    }

    /**
     * 사용자의 공개 강의 목록 조회 (DTO 반환)
     * 트랜잭션 내부에서 조회 및 변환 수행
     */
    @Transactional(readOnly = true)
    public List<LectureResponse> getUserPublicLectures(Long userId) {
        List<Lecture> lectures = lectureRepository.findByAuthorIdAndIsPublicTrue(userId);
        // 트랜잭션 내부에서 DTO 변환
        return lectures.stream()
                .map(LectureResponse::from)
                .toList();
    }

    /**
     * 사용자의 공개 강의 목록 조회 (페이징, DTO 반환)
     * 트랜잭션 내부에서 조회 및 변환 수행
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getUserPublicLectures(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Lecture> lecturesPage = lectureRepository.findByAuthorIdAndIsPublicTrue(userId, pageable);

        // 트랜잭션 내부에서 DTO 변환
        List<LectureResponse> responses = lecturesPage.getContent().stream()
                .map(LectureResponse::from)
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("lectures", responses);
        response.put("meta", createPaginationMeta(lecturesPage));
        return response;
    }

    // === 검증 메서드 ===

    /**
     * 강의 기본 검증
     */
    private void validateLecture(Lecture lecture) {
        if (lecture.getTitle() == null || lecture.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("강의 제목은 필수입니다.");
        }

        if (lecture.getTitle().length() > 200) {
            throw new IllegalArgumentException("강의 제목은 200자를 초과할 수 없습니다.");
        }

        if (lecture.getType() == null) {
            throw new IllegalArgumentException("강의 유형은 필수입니다.");
        }

    }

    // === 페이지네이션 헬퍼 메서드 ===

    /**
     * 페이지네이션 메타데이터 생성
     */
    private Map<String, Object> createPaginationMeta(Page<?> page) {
        Map<String, Object> meta = new HashMap<>();
        meta.put("current_page", page.getNumber() + 1);  // 1부터 시작
        meta.put("next_page", page.hasNext() ? page.getNumber() + 2 : null);
        meta.put("prev_page", page.hasPrevious() ? page.getNumber() : null);
        meta.put("total_pages", page.getTotalPages());
        meta.put("total_count", page.getTotalElements());
        meta.put("per_page", page.getSize());
        return meta;
    }

    // === Controller용 DTO 변환 메서드 ===

    /**
     * 모든 강의 조회 후 DTO로 변환
     * 트랜잭션 내부에서 Lazy Loading된 컬렉션을 안전하게 변환
     */
    @Transactional(readOnly = true)
    public List<LectureResponse> getAllLectures() {
        List<Lecture> lectures = lectureRepository.findAllWithTestCases();
        // 트랜잭션 내부에서 DTO 변환 → testCases JOIN FETCH로 미리 로딩
        return lectures.stream()
                .map(LectureResponse::from)
                .toList();
    }

    /**
     * 강의 조회 후 DTO로 변환 (권한 체크 포함)
     * 트랜잭션 내부에서 권한 체크 및 DTO 변환 수행
     */
    @Transactional(readOnly = true)
    public LectureResponse getLecture(Long id, Long userId) {
        Lecture lecture = lectureRepository.findByIdWithTestCases(id)
                .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다: " + id));
        // 트랜잭션 내부에서 권한 체크
        if (!lecture.isPublicLecture() && (userId == null || !lecture.isAuthor(userId))) {
            throw new AccessDeniedException("이 강의에 접근할 권한이 없습니다.");
        }

        // 트랜잭션 내부에서 DTO 변환
        return LectureResponse.from(lecture);
    }

    /**
     * 강의 생성 후 DTO로 변환
     * 트랜잭션 내부에서 생성 및 변환 수행
     */
    @Transactional
    public LectureResponse createLectureWithResponse(CreateLectureRequest request, User author) {
        Lecture lecture = Lecture.from(request, author);
        lecture = lectureRepository.save(lecture);
        // 트랜잭션 내부에서 DTO 변환
        return LectureResponse.from(lecture);
    }

    /**
     * 강의 수정 (DTO 반환)
     * 트랜잭션 내부에서 수정 및 변환 수행
     */
    @Transactional
    public LectureResponse updateLectureWithResponse(Long id, CreateLectureRequest request, Long userId) {
        Lecture lecture = updateLecture(id, request, userId);
        // 트랜잭션 내부에서 DTO 변환
        return LectureResponse.from(lecture);
    }
}
