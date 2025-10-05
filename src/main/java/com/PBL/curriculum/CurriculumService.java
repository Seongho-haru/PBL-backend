package com.PBL.curriculum;

import com.PBL.lecture.Lecture;
import com.PBL.lecture.LectureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 커리큘럼 서비스
 * 커리큘럼과 강의 연결 관리 비즈니스 로직
 */
@Service
@Transactional
public class CurriculumService {

    private final CurriculumRepository curriculumRepository;
    private final CurriculumLectureRepository curriculumLectureRepository;
    private final LectureRepository lectureRepository;

    @Autowired
    public CurriculumService(
            CurriculumRepository curriculumRepository,
            CurriculumLectureRepository curriculumLectureRepository,
            LectureRepository lectureRepository) {
        this.curriculumRepository = curriculumRepository;
        this.curriculumLectureRepository = curriculumLectureRepository;
        this.lectureRepository = lectureRepository;
    }

    // === 커리큘럼 기본 CRUD ===

    /**
     * 모든 커리큘럼 조회 (강의 포함)
     */
    @Transactional(readOnly = true)
    public List<Curriculum> getAllCurriculums() {
        return curriculumRepository.findAllWithLectures();
    }

    /**
     * 공개 커리큘럼만 조회
     */
    @Transactional(readOnly = true)
    public List<Curriculum> getPublicCurriculums() {
        return curriculumRepository.findPublicCurriculumsWithLectures();
    }

    /**
     * ID로 커리큘럼 상세 조회
     */
    @Transactional(readOnly = true)
    public Optional<Curriculum> getCurriculumById(Long id) {
        return curriculumRepository.findByIdWithLectures(id);
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
     * 커리큘럼 수정
     */
    public Curriculum updateCurriculum(Long id, String title, String description, Boolean isPublic) {
        Curriculum curriculum = curriculumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("커리큘럼을 찾을 수 없습니다: " + id));
        
        if (title != null) curriculum.setTitle(title);
        if (description != null) curriculum.setDescription(description);
        if (isPublic != null) curriculum.setIsPublic(isPublic);
        
        return curriculumRepository.save(curriculum);
    }

    /**
     * 커리큘럼 삭제
     */
    public void deleteCurriculum(Long id) {
        if (!curriculumRepository.existsById(id)) {
            throw new RuntimeException("커리큘럼을 찾을 수 없습니다: " + id);
        }
        curriculumRepository.deleteById(id);
    }

    // === 강의 연결 관리 ===

    /**
     * 커리큘럼에 강의 추가
     */
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
     * 모든 공개 강의 조회
     */
    @Transactional(readOnly = true)
    public List<Lecture> getPublicLectures() {
        return lectureRepository.findByIsPublicTrueOrderByCreatedAtDesc();
    }

    /**
     * 공개 강의 검색
     */
    @Transactional(readOnly = true)
    public List<Lecture> searchPublicLectures(String title, String category, String difficulty, String type) {
        return lectureRepository.findPublicLecturesBySearchCriteria(
                title, 
                category, 
                difficulty, 
                type != null ? com.PBL.lecture.LectureType.valueOf(type.toUpperCase()) : null
        );
    }

    // === 커리큘럼 검색 ===

    /**
     * 커리큘럼 제목으로 검색
     */
    @Transactional(readOnly = true)
    public List<Curriculum> searchCurriculums(String title) {
        return curriculumRepository.findByTitleContainingIgnoreCaseOrderByCreatedAtDesc(title);
    }

    /**
     * 공개 커리큘럼 제목으로 검색
     */
    @Transactional(readOnly = true)
    public List<Curriculum> searchPublicCurriculums(String title) {
        return curriculumRepository.findByIsPublicTrueAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(title);
    }

    // === 강의 삭제 시 자동 정리 ===

    /**
     * 강의가 삭제될 때 모든 커리큘럼에서 해당 강의 제거
     * (LectureService에서 호출됨)
     */
    public void removeLectureFromAllCurriculums(Long lectureId) {
        curriculumLectureRepository.deleteByLectureId(lectureId);
    }

    // === 커리큘럼 공개/비공개 설정 ===

    /**
     * 커리큘럼 공개
     */
    public void publishCurriculum(Long id) {
        Curriculum curriculum = curriculumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("커리큘럼을 찾을 수 없습니다: " + id));
        
        curriculum.publish();
        curriculumRepository.save(curriculum);
    }

    /**
     * 커리큘럼 비공개
     */
    public void unpublishCurriculum(Long id) {
        Curriculum curriculum = curriculumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("커리큘럼을 찾을 수 없습니다: " + id));
        
        curriculum.unpublish();
        curriculumRepository.save(curriculum);
    }
}
