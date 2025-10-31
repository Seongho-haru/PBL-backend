package com.PBL.search;

import com.PBL.lecture.LectureType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 통합 검색 API 컨트롤러
 * 커리큘럼과 강의를 동시에 검색
 */
@RestController
@RequestMapping("/api/search")
@Tag(name = "Search", description = "통합 검색 API")
public class SearchController {

    @Autowired
    private SearchService searchService;

    /**
     * 통합 검색
     * GET /api/search
     * 
     * 커리큘럼과 강의를 동시에 검색합니다.
     * 공개된 콘텐츠만 검색됩니다.
     */
    @GetMapping
    @Operation(
            summary = "통합 검색",
            description = "커리큘럼과 강의를 동시에 검색합니다. 공개된 콘텐츠만 검색됩니다."
    )
    public ResponseEntity<Map<String, Object>> unifiedSearch(
            @Parameter(description = "검색할 제목 (부분 일치, 필수)") @RequestParam(required = false) String title,
            @Parameter(description = "카테고리 필터 (강의만 적용)") @RequestParam(required = false) String category,
            @Parameter(description = "난이도 필터 (강의만 적용)") @RequestParam(required = false) String difficulty,
            @Parameter(description = "강의 유형 필터 (강의만 적용)") @RequestParam(required = false) String type,
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size) {

        // 타입 파라미터 변환
        LectureType lectureType = null;
        if (type != null && !type.trim().isEmpty()) {
            try {
                lectureType = LectureType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                // 잘못된 타입이면 null로 처리
                lectureType = null;
            }
        }

        Map<String, Object> result = searchService.unifiedSearch(
                title, category, difficulty, lectureType, page, size);

        return ResponseEntity.ok(result);
    }
}

