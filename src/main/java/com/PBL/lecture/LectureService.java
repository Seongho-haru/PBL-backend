package com.PBL.lecture;
import com.PBL.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 강의 관리 서비스
 * 강의의 생성, 조회, 수정, 삭제 및 비즈니스 로직 처리
 */
@Service
@Transactional(readOnly = true)
public class LectureService {

    private final LectureRepository lectureRepository;

    public LectureService(LectureRepository lectureRepository) {
        this.lectureRepository = lectureRepository;
    }

    // === 기본 CRUD ===

    /**
     * 강의 생성
     */
    @Transactional
    public Lecture createLecture(Lecture lecture) {
        validateLecture(lecture);
        return lectureRepository.save(lecture);
    }

    /**
     * 강의 생성 (작성자 포함)
     */
    @Transactional
    public Lecture createLecture(Lecture lecture, User author) {
        lecture.setAuthor(author);
        validateLecture(lecture);
        return lectureRepository.save(lecture);
    }

    /**
     * 강의 생성 (파라미터 버전)
     */
    @Transactional
    public Lecture createLecture(String title, String description, LectureType type,
                                 String category, String difficulty) {
        Lecture lecture = new Lecture(title, description, type);
        lecture.setCategory(category);
        lecture.setDifficulty(difficulty);

        validateLecture(lecture);
        return lectureRepository.save(lecture);
    }

    /**
     * 강의 생성 (파라미터 버전, 작성자 포함)
     */
    @Transactional
    public Lecture createLecture(String title, String description, LectureType type,
                                 String category, String difficulty, User author) {
        Lecture lecture = new Lecture(title, description, type);
        lecture.setCategory(category);
        lecture.setDifficulty(difficulty);
        lecture.setAuthor(author);

        validateLecture(lecture);
        return lectureRepository.save(lecture);
    }

    /**
     * 문제 강의 생성 (시간/메모리 제한 포함)
     */
    @Transactional
    public Lecture createProblemLecture(String title, String description, String category,
                                        String difficulty, Integer timeLimit, Integer memoryLimit) {
        Lecture lecture = new Lecture(title, description, LectureType.PROBLEM);
        lecture.setCategory(category);
        lecture.setDifficulty(difficulty);
        lecture.setTimeLimit(timeLimit);
        lecture.setMemoryLimit(memoryLimit);

        validateProblemLecture(lecture);
        return lectureRepository.save(lecture);
    }

    /**
     * 문제 강의 생성 (시간/메모리 제한 포함, 작성자 포함)
     */
    @Transactional
    public Lecture createProblemLecture(String title, String description, String category,
                                        String difficulty, Integer timeLimit, Integer memoryLimit, User author) {
        Lecture lecture = new Lecture(title, description, LectureType.PROBLEM);
        lecture.setCategory(category);
        lecture.setDifficulty(difficulty);
        lecture.setTimeLimit(timeLimit);
        lecture.setMemoryLimit(memoryLimit);
        lecture.setAuthor(author);

        validateProblemLecture(lecture);
        return lectureRepository.save(lecture);
    }

    /**
     * 강의 상세 조회 (테스트케이스 포함)
     */
    public Optional<Lecture> getLectureWithTestCases(Long id) {
        return lectureRepository.findByIdWithTestCases(id);
    }

    /**
     * 강의 조회
     */
    public Optional<Lecture> getLecture(Long id) {
        return lectureRepository.findById(id);
    }

    /**
     * 모든 강의 조회 (최신순)
     * N+1 쿼리 문제 해결을 위해 최적화된 쿼리 사용
     */
    public List<Lecture> getAllLectures() {
        return lectureRepository.findAllByOrderByCreatedAtDesc();
    }

    /**
     * 강의 수정
     */
    @Transactional
    public Lecture updateLecture(Long id, Lecture updatedLecture) {
        Lecture existingLecture = lectureRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다: " + id));

        // 기본 정보 업데이트
        existingLecture.setTitle(updatedLecture.getTitle());
        existingLecture.setDescription(updatedLecture.getDescription());
        existingLecture.setCategory(updatedLecture.getCategory());
        existingLecture.setDifficulty(updatedLecture.getDifficulty());

        // 문제 타입인 경우 제한시간 업데이트
        if (existingLecture.isProblemType()) {
            existingLecture.setTimeLimit(updatedLecture.getTimeLimit());
            existingLecture.setMemoryLimit(updatedLecture.getMemoryLimit());
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
     * 유형별 강의 조회
     */
    public List<Lecture> getLecturesByType(LectureType type) {
        return lectureRepository.findByType(type);
    }

    /**
     * 카테고리별 강의 조회
     */
    public List<Lecture> getLecturesByCategory(String category) {
        return lectureRepository.findByCategory(category);
    }

    /**
     * 제목으로 강의 검색
     */
    public List<Lecture> searchLecturesByTitle(String title) {
        return lectureRepository.findByTitleContainingIgnoreCase(title);
    }

    /**
     * 복합 검색 (페이징)
     */
    public Page<Lecture> searchLectures(String title, String category, String difficulty,
                                        LectureType type, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return lectureRepository.findBySearchCriteria(title, category, difficulty, type, pageable);
    }

    /**
     * 최근 강의 조회 (10개)
     */
    public List<Lecture> getRecentLectures() {
        return lectureRepository.findTop10ByOrderByCreatedAtDesc();
    }

    // === 테스트케이스 관리 ===

    /**
     * 테스트케이스 추가
     */
    @Transactional
    public Lecture addTestCase(Long lectureId, String input, String expectedOutput) {
        Lecture lecture = lectureRepository.findByIdWithTestCases(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다: " + lectureId));

        if (!lecture.isProblemType()) {
            throw new IllegalArgumentException("문제 타입 강의에만 테스트케이스를 추가할 수 있습니다.");
        }

        lecture.addTestCase(input, expectedOutput);
        return lectureRepository.save(lecture);
    }

    /**
     * 모든 테스트케이스 제거
     */
    @Transactional
    public Lecture clearTestCases(Long lectureId) {
        Lecture lecture = lectureRepository.findByIdWithTestCases(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다: " + lectureId));

        lecture.clearTestCases();
        return lectureRepository.save(lecture);
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
     * 모든 공개 강의 조회
     */
    public List<Lecture> getPublicLectures() {
        return lectureRepository.findByIsPublicTrueOrderByCreatedAtDesc();
    }

    /**
     * 공개 강의 검색
     */
    public List<Lecture> searchPublicLectures(String title, String category, String difficulty, LectureType type) {
        return lectureRepository.findPublicLecturesBySearchCriteria(title, category, difficulty, type, 
                type != null ? type.name() : null);
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
     * 사용자의 강의 목록 조회
     */
    public List<Lecture> getUserLectures(Long userId) {
        return lectureRepository.findByAuthorId(userId);
    }

    /**
     * 사용자의 공개 강의 목록 조회
     */
    public List<Lecture> getUserPublicLectures(Long userId) {
        return lectureRepository.findByAuthorIdAndIsPublicTrue(userId);
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

        // 문제 타입 추가 검증
        if (lecture.isProblemType()) {
            validateProblemLecture(lecture);
        }
    }

    /**
     * 문제 강의 추가 검증
     */
    private void validateProblemLecture(Lecture lecture) {
        if (lecture.getTimeLimit() != null && lecture.getTimeLimit() <= 0) {
            throw new IllegalArgumentException("시간 제한은 0보다 커야 합니다.");
        }

        if (lecture.getMemoryLimit() != null && lecture.getMemoryLimit() <= 0) {
            throw new IllegalArgumentException("메모리 제한은 0보다 커야 합니다.");
        }

        // 최대 제한값 체크
        if (lecture.getTimeLimit() != null && lecture.getTimeLimit() > 300) {
            throw new IllegalArgumentException("시간 제한은 300초를 초과할 수 없습니다.");
        }

        if (lecture.getMemoryLimit() != null && lecture.getMemoryLimit() > 1024) {
            throw new IllegalArgumentException("메모리 제한은 1024MB를 초과할 수 없습니다.");
        }
    }
}
