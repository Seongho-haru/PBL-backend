package com.PBL.search;

import com.PBL.curriculum.CurriculumService;
import com.PBL.lecture.LectureService;
import com.PBL.lecture.LectureType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * 통합 검색 서비스
 * 커리큘럼과 강의를 동시에 검색하는 기능 제공
 */
@Service
@Transactional(readOnly = true)
public class SearchService {

    @Autowired
    private CurriculumService curriculumService;

    @Autowired
    private LectureService lectureService;

    /**
     * 통합 검색 (공개 커리큘럼 + 공개 강의)
     * 
     * @param title 제목 검색 키워드 (필수)
     * @param category 카테고리 필터 (강의만 적용, 선택)
     * @param difficulty 난이도 필터 (강의만 적용, 선택)
     * @param type 강의 유형 필터 (강의만 적용, 선택)
     * @param page 페이지 번호 (기본값: 0)
     * @param size 페이지 크기 (기본값: 10)
     * @return 커리큘럼과 강의 검색 결과
     */
    public Map<String, Object> unifiedSearch(
            String title,
            String category,
            String difficulty,
            LectureType type,
            Boolean isPublic,
            int page,
            int size) {

        // 제목이 없으면 빈 결과 반환
        if (title == null || title.trim().isEmpty()) {
            Map<String, Object> emptyResult = new HashMap<>();
            emptyResult.put("curriculums", Map.of(
                    "curriculums", java.util.Collections.emptyList(),
                    "meta", createEmptyMeta()
            ));
            emptyResult.put("lectures", Map.of(
                    "lectures", java.util.Collections.emptyList(),
                    "meta", createEmptyMeta()
            ));
            return emptyResult;
        }

        // 커리큘럼 검색 (공개 여부 필터 적용)
        // isPublic이 null이면 공개만 검색 (기존 동작 유지)
        Boolean curriculumIsPublic = isPublic != null ? isPublic : true;
        Map<String, Object> curriculumResult = curriculumService.searchCurriculums(title.trim(), curriculumIsPublic, page, size);

        // 강의 검색 (공개 여부 필터 적용)
        // isPublic이 null이면 공개만 검색 (기존 동작 유지)
        Boolean lectureIsPublic = isPublic != null ? isPublic : true;
        Map<String, Object> lectureResult = lectureService.searchPublicLectures(
                title.trim(), category, difficulty, type, lectureIsPublic, page, size);

        // 결과 합치기
        Map<String, Object> result = new HashMap<>();
        result.put("curriculums", curriculumResult);
        result.put("lectures", lectureResult);

        return result;
    }

    /**
     * 빈 페이징 메타데이터 생성
     */
    private Map<String, Object> createEmptyMeta() {
        Map<String, Object> meta = new HashMap<>();
        meta.put("currentPage", 0);
        meta.put("totalElements", 0);
        meta.put("totalPages", 0);
        meta.put("hasNext", false);
        meta.put("hasPrevious", false);
        return meta;
    }
}

