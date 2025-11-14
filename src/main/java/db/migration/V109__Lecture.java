package db.migration;

import com.fasterxml.jackson.databind.ObjectMapper;
import db.dto.CurriculumDto;
import db.dto.LectureExpandedDto;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.List;

import static db.utile.readJsonFile;

public class V109__Lecture extends BaseJavaMigration {

    private static int successCount = 0;
    private static int errorCount = 0;

    @Override
    public void migrate(Context context) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Connection connection = context.getConnection();

        // 1. Curriculum JSON 읽기 및 step_number → title 매핑 생성
        String curriculumFile = "db/json/study/curriculums.json";
        List<CurriculumDto> curriculums = readJsonFile(mapper, curriculumFile, CurriculumDto.class);

        java.util.Map<Long, String> stepToTitleMap = new java.util.HashMap<>();
        for (CurriculumDto curriculum : curriculums) {
            if (curriculum.getStepNumber() != null && curriculum.getStepTitle() != null) {
                stepToTitleMap.put((long) curriculum.getStepNumber(), curriculum.getStepTitle());
            }
        }

        // 2. DB의 curriculum title → ID 매핑 생성
        java.util.Map<String, Long> titleToIdMap = new java.util.HashMap<>();
        String curriculumQuery = "SELECT id, title FROM curriculums";
        try (PreparedStatement stmt = connection.prepareStatement(curriculumQuery);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Long id = rs.getLong("id");
                String title = rs.getString("title");
                titleToIdMap.put(title, id);
            }
        }

        // 3. step_number → DB ID 최종 매핑 생성
        java.util.Map<Long, Long> curriculumIdMapping = new java.util.HashMap<>();
        for (java.util.Map.Entry<Long, String> entry : stepToTitleMap.entrySet()) {
            Long stepNumber = entry.getKey();
            String title = entry.getValue();
            Long dbId = titleToIdMap.get(title);
            if (dbId != null) {
                curriculumIdMapping.put(stepNumber, dbId);
            } else {
                System.err.println(String.format("⚠️  step %d의 커리큘럼 '%s'을 DB에서 찾을 수 없음",
                    stepNumber, title));
            }
        }
        System.out.println(String.format("📋 커리큘럼 매핑 완료: %d개\n", curriculumIdMapping.size()));

        // 2. JSON 파일 읽기
        String lectureFile = "db/json/study/lectures.json";
        List<LectureExpandedDto> lectures = readJsonFile(mapper, lectureFile, LectureExpandedDto.class);

        System.out.println(String.format("📚 총 %d개의 강의를 로딩합니다...\n", lectures.size()));

        int currentCount = 0;
        int totalCount = lectures.size();

        // 3. 각 강의 처리
        for (LectureExpandedDto lecture : lectures) {
            java.sql.Savepoint savepoint = null;
            try {
                currentCount++;

                // 제목이 없는 경우 건너뛰기
                if (lecture.getTitle() == null || lecture.getTitle().isEmpty()) {
                    System.err.println(String.format("⚠️  [%d/%d] 제목 없음",
                            currentCount, totalCount));
                    errorCount++;
                    continue;
                }

                // 세이브포인트 생성
                savepoint = connection.setSavepoint("lecture_" + currentCount);

                // 3. Lecture 삽입
                String lectureSql = "INSERT INTO lectures (title, description, type, category, difficulty, " +
                        "is_public, author_id, duration_minutes, learning_objectives, thumbnail_image_url, " +
                        "content, created_at, updated_at) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                Long lectureId;
                try (PreparedStatement stmt = connection.prepareStatement(lectureSql,
                        PreparedStatement.RETURN_GENERATED_KEYS)) {

                    stmt.setString(1, lecture.getTitle());
                    stmt.setString(2, truncate(lecture.getDescription(), 65535));
                    stmt.setString(3, truncate(lecture.getType(), 20));
                    stmt.setString(4, truncate(lecture.getCategory(), 100));
                    stmt.setString(5, truncate(lecture.getDifficulty(), 20));
                    stmt.setBoolean(6, lecture.getIsPublic() != null ? lecture.getIsPublic() : false);
                    stmt.setLong(7, lecture.getAuthorId() != null ? lecture.getAuthorId() : 1L);
                    stmt.setObject(8, lecture.getDurationMinutes());
                    stmt.setString(9, truncate(lecture.getLearningObjectives(), 65535));
                    stmt.setString(10, truncate(lecture.getThumbnailImageUrl(), 500));
                    stmt.setString(11, lecture.getContent()); // content는 TEXT 타입이므로 길이 제한 없음
                    stmt.setObject(12, LocalDateTime.now()); // created_at
                    stmt.setObject(13, LocalDateTime.now()); // updated_at

                    stmt.executeUpdate();

                    // Lecture ID 가져오기
                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            lectureId = generatedKeys.getLong(1);
                        } else {
                            throw new Exception("Creating lecture failed, no ID obtained.");
                        }
                    }
                }

                // 4. Tags 삽입
                if (lecture.getTags() != null && !lecture.getTags().isEmpty()) {
                    insertTags(connection, lectureId, lecture.getTags());
                }

                // 5. CurriculumLecture 연결 (curriculum_id가 있는 경우)
                if (lecture.getCurriculumId() != null) {
                    // JSON의 curriculum_id(step_number)를 실제 DB ID로 변환
                    Long actualCurriculumId = curriculumIdMapping.get(lecture.getCurriculumId());
                    if (actualCurriculumId != null) {
                        insertCurriculumLecture(connection, actualCurriculumId, lectureId);
                    } else {
                        System.err.println(String.format("⚠️  커리큘럼 ID %d에 해당하는 실제 ID를 찾을 수 없음",
                                lecture.getCurriculumId()));
                    }
                }

                // 세이브포인트 해제 (성공)
                if (savepoint != null) {
                    connection.releaseSavepoint(savepoint);
                }

                successCount++;

                // 진행률 표시
                if (currentCount % Math.max(1, totalCount / 10) == 0 || currentCount == totalCount) {
                    int percent = (currentCount * 100) / totalCount;
                    int barLength = 50;
                    int filled = (currentCount * barLength) / totalCount;
                    String bar = "█".repeat(filled) + "░".repeat(barLength - filled);

                    System.out.println(String.format("[%s] %d%% (%d/%d) - 성공: %d, 오류: %d",
                            bar, percent, currentCount, totalCount, successCount, errorCount));
                }

            } catch (Exception e) {
                // 세이브포인트 롤백
                if (savepoint != null) {
                    try {
                        connection.rollback(savepoint);
                    } catch (Exception rollbackEx) {
                        System.err.println(String.format("롤백 실패: %s", rollbackEx.getMessage()));
                    }
                }
                errorCount++;
                System.err.println(String.format("✗ [%d/%d] 실패: %s - %s",
                        currentCount, totalCount, lecture.getTitle(), e.getMessage()));
            }
        }

        System.out.println(String.format("\n✓ 완료: 성공 %d개, 오류 %d개, 총 %d개 처리",
                successCount, errorCount, totalCount));
    }

    /**
     * 문자열 길이 제한
     */
    private String truncate(String str, int maxLength) {
        if (str == null) {
            return null;
        }
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength);
    }

    /**
     * Tags 삽입
     */
    private void insertTags(Connection connection, Long lectureId, List<String> tags) throws Exception {
        String sql = "INSERT INTO lecture_tags (lecture_id, tag) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (String tag : tags) {
                if (tag != null && !tag.trim().isEmpty()) {
                    stmt.setLong(1, lectureId);
                    stmt.setString(2, truncate(tag, 50));
                    stmt.addBatch();
                }
            }
            stmt.executeBatch();
        }
    }

    /**
     * CurriculumLecture 연결 생성
     */
    private void insertCurriculumLecture(Connection connection, Long curriculumId, Long lectureId) throws Exception {
        // 현재 커리큘럼의 마지막 order_index 조회
        String maxOrderSql = "SELECT COALESCE(MAX(order_index), 0) FROM curriculum_lectures WHERE curriculum_id = ?";
        int nextOrder = 1;

        try (PreparedStatement stmt = connection.prepareStatement(maxOrderSql)) {
            stmt.setLong(1, curriculumId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    nextOrder = rs.getInt(1) + 1;
                }
            }
        }

        // CurriculumLecture 삽입
        String sql = "INSERT INTO curriculum_lectures (curriculum_id, lecture_id, is_required, order_index, " +
                "original_author, source_info) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, curriculumId);
            stmt.setLong(2, lectureId);
            stmt.setBoolean(3, true); // is_required 기본값 true
            stmt.setInt(4, nextOrder);
            stmt.setString(5, null); // original_author
            stmt.setString(6, null); // source_info
            stmt.executeUpdate();
        }
    }
}
