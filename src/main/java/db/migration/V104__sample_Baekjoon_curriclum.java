package db.migration;

import com.fasterxml.jackson.databind.ObjectMapper;
import db.dto.BaekjoonCurriclumDTO;
import db.dto.BaekjoonProblemDto;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static db.utile.readJsonFile;

public class V104__sample_Baekjoon_curriclum extends BaseJavaMigration {

    private static int successCount = 0;

    @Override
    public void migrate(Context context) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Connection connection = context.getConnection();

        // 1. 시스템 관리자 계정 1번 사용
        Long authorId = 1L;

        // 2. 백준 문제 JSON 파일 읽어서 problemId → 문제 정보 매핑 생성
        List<BaekjoonProblemDto> baekjoonProblems = readJsonFile(
                mapper,
                "db/json/baekjoon/baekjoon_problems.json",
                BaekjoonProblemDto.class);

        Map<Long, BaekjoonProblemDto> problemIdToInfo = new HashMap<>();
        for (BaekjoonProblemDto problem : baekjoonProblems) {
            if (problem.getMetadata() != null && problem.getMetadata().get("problemId") != null) {
                Long problemId = ((Number) problem.getMetadata().get("problemId")).longValue();
                problemIdToInfo.put(problemId, problem);
            }
        }

        // 3. 커리큘럼 JSON 파일 읽기
        List<BaekjoonCurriclumDTO> baekjoonCurriculums = readJsonFile(
                mapper,
                "db/json/baekjoon/baekjoon_steps.json",
                BaekjoonCurriclumDTO.class);

        // 4. 커리큘럼 삽입 SQL
        String curriculumSql = "INSERT INTO curriculums (title, description, duration_minutes, "
                + "thumbnail_image_url, category, is_public, difficulty, summary, "
                + "average_rating, student_count, author_id, created_at, updated_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        int total = baekjoonCurriculums.size();
        int current = 0;
        try (PreparedStatement curriculumStmt = connection.prepareStatement(curriculumSql,
                Statement.RETURN_GENERATED_KEYS)) {

            for (BaekjoonCurriclumDTO curriculum : baekjoonCurriculums) {
                try {
                    LocalDateTime now = LocalDateTime.now();

                    // 5. Curriculum 삽입
                    curriculumStmt.setString(1, curriculum.getTitle());
                    curriculumStmt.setString(2, curriculum.getDescription());
                    curriculumStmt.setInt(3, curriculum.getDurationMinutes());
                    curriculumStmt.setString(4, curriculum.getThumbnailImageUrl());
                    curriculumStmt.setString(5, curriculum.getCategory());
                    curriculumStmt.setBoolean(6, true); // 백준 커리큘럼은 기본 공개
                    curriculumStmt.setString(7, curriculum.getDifficulty());
                    curriculumStmt.setString(8, curriculum.getSummary());
                    curriculumStmt.setBigDecimal(9, java.math.BigDecimal.valueOf(0.0));
                    curriculumStmt.setInt(10, 0);
                    curriculumStmt.setLong(11, authorId);
                    curriculumStmt.setObject(12, now);
                    curriculumStmt.setObject(13, now);

                    curriculumStmt.executeUpdate();

                    // 6. Curriculum ID 가져오기
                    Long curriculumId;
                    try (ResultSet generatedKeys = curriculumStmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            curriculumId = generatedKeys.getLong(1);
                        } else {
                            throw new Exception("Creating curriculum failed, no ID obtained.");
                        }
                    }

                    // 7. Tags 삽입
                    if (curriculum.getTags() != null && !curriculum.getTags().isEmpty()) {
                        String tagSql = "INSERT INTO curriculum_tags (curriculum_id, tag) VALUES (?, ?)";
                        try (PreparedStatement tagStmt = connection.prepareStatement(tagSql)) {
                            for (String tag : curriculum.getTags()) {
                                tagStmt.setLong(1, curriculumId);
                                tagStmt.setString(2, tag);
                                tagStmt.addBatch();
                            }
                            tagStmt.executeBatch();
                        }
                    }

                    // 8. CurriculumLecture 삽입 (problemId로 Lecture ID 매핑)
                    if (curriculum.getProblemIds() != null && !curriculum.getProblemIds().isEmpty()) {
                        String curriculumLectureSql = "INSERT INTO curriculum_lectures "
                                + "(curriculum_id, lecture_id, order_index, is_required, created_at) "
                                + "VALUES (?, ?, ?, ?, ?)";

                        try (PreparedStatement clStmt = connection.prepareStatement(curriculumLectureSql)) {
                            int orderIndex = 1;
                            java.util.Set<Long> addedLectureIds = new java.util.HashSet<>();

                            for (int i = 0; i < curriculum.getProblemIds().size(); i++) {
                                Long problemId = curriculum.getProblemIds().get(i);

                                // problemId로 문제 정보 가져오기
                                BaekjoonProblemDto problemInfo = problemIdToInfo.get(problemId);

                                if (problemInfo == null) {
                                    System.err.println(String.format("Warning: Problem info not found for problem ID: %s", problemId));
                                    continue;
                                }

                                // 문제 정보로 Lecture ID 조회 (title + description으로 정확히 매칭)
                                Long lectureId = findLectureIdByProblemInfo(connection, problemInfo);

                                if (lectureId != null) {
                                    // 중복 체크: 이미 추가된 lecture_id는 건너뛰기
                                    if (addedLectureIds.contains(lectureId)) {
                                        System.err.println(String.format("Warning: Skipping duplicate lecture '%s' (ID: %d, problem ID: %s)",
                                            problemInfo.getTitle(), lectureId, problemId));
                                        continue;
                                    }

                                    clStmt.setLong(1, curriculumId);
                                    clStmt.setLong(2, lectureId);
                                    clStmt.setInt(3, orderIndex++);
                                    clStmt.setBoolean(4, true);
                                    clStmt.setObject(5, now);
                                    clStmt.addBatch();
                                    addedLectureIds.add(lectureId);
                                } else {
                                    System.err.println(String.format("Warning: Lecture not found for problem: '%s' (ID: %s)",
                                        problemInfo.getTitle(), problemId));
                                }
                            }
                            clStmt.executeBatch();
                        }
                    }

                    successCount++;
                    current++;
                    int percent = (current * 100) / total;

                    // 프로그레스 바
                    int barLength = 50;
                    int filled = (current * barLength) / total;
                    String bar = "█".repeat(filled) + "░".repeat(barLength - filled);

                    System.out.println(String.format("[%s] %d%% (%d/%d) - %s",
                            bar, percent, current, total, curriculum.getTitle()));

                } catch (Exception e) {
                    current++;
                    System.err.println(String.format("✗ [%d/%d] Failed: %s - %s",
                            current, total, curriculum.getTitle(), e.getMessage()));
                }
            }
        }

        System.out.println(String.format("\n✓ Successfully loaded %d/%d curriculums", successCount, total));
    }

    /**
     * 문제 정보로 Lecture ID 조회 (title + description으로 정확히 매칭)
     */
    private Long findLectureIdByProblemInfo(Connection connection, BaekjoonProblemDto problemInfo) throws Exception {
        // Lecture 테이블에서 title, description, category로 정확히 검색
        String sql = "SELECT l.id FROM lectures l "
                + "WHERE l.category = '알고리즘' "
                + "AND l.title = ? "
                + "AND l.description = ? "
                + "LIMIT 1";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, problemInfo.getTitle());
            stmt.setString(2, problemInfo.getDescription());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id");
                }
            }
        }

        return null;
    }
}
