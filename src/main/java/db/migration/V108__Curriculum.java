package db.migration;

import com.fasterxml.jackson.databind.ObjectMapper;
import db.dto.CurriculumDto;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.List;

import static db.utile.readJsonFile;

public class V108__Curriculum extends BaseJavaMigration {

    private static int successCount = 0;
    private static int errorCount = 0;

    @Override
    public void migrate(Context context) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Connection connection = context.getConnection();

        // 1. JSON 파일 읽기
        String curriculumFile = "db/json/study/curriculums.json";
        List<CurriculumDto> curriculums = readJsonFile(mapper, curriculumFile, CurriculumDto.class);

        System.out.println(String.format("\n📚 총 %d개의 커리큘럼을 로딩합니다...\n", curriculums.size()));

        int currentCount = 0;
        int totalCount = curriculums.size();

        // 2. 각 커리큘럼 처리
        for (CurriculumDto curriculum : curriculums) {
            java.sql.Savepoint savepoint = null;
            try {
                currentCount++;

                // 제목이 없는 경우 건너뛰기
                if (curriculum.getStepTitle() == null || curriculum.getStepTitle().isEmpty()) {
                    System.err.println(String.format("⚠️  [%d/%d] 제목 없음: step %d",
                            currentCount, totalCount, curriculum.getStepNumber()));
                    errorCount++;
                    continue;
                }

                // 세이브포인트 생성
                savepoint = connection.setSavepoint("curriculum_" + currentCount);

                // 3. Curriculum 삽입
                String curriculumSql = "INSERT INTO curriculums (title, description, is_public, author_id, " +
                        "category, difficulty, summary, duration_minutes, learning_objectives, " +
                        "thumbnail_image_url, average_rating, student_count, created_at, updated_at) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                Long curriculumId;
                try (PreparedStatement stmt = connection.prepareStatement(curriculumSql,
                        PreparedStatement.RETURN_GENERATED_KEYS)) {

                    stmt.setString(1, curriculum.getStepTitle());
                    stmt.setString(2, truncate(curriculum.getDescription(), 65535));
                    stmt.setBoolean(3, curriculum.getIsPublic() != null ? curriculum.getIsPublic() : false);
                    stmt.setLong(4, curriculum.getAuthorId() != null ? curriculum.getAuthorId() : 1L);
                    stmt.setString(5, truncate(curriculum.getCategory(), 100));
                    stmt.setString(6, truncate(curriculum.getDifficulty(), 20));
                    stmt.setString(7, truncate(curriculum.getSummary(), 500));
                    stmt.setObject(8, curriculum.getDurationMinutes());
                    stmt.setString(9, truncate(curriculum.getLearningObjectives(), 65535));
                    stmt.setString(10, truncate(curriculum.getThumbnailImageUrl(), 500));
                    stmt.setBigDecimal(11, java.math.BigDecimal.ZERO); // average_rating 기본값
                    stmt.setInt(12, 0); // student_count 기본값
                    stmt.setObject(13, LocalDateTime.now()); // created_at
                    stmt.setObject(14, LocalDateTime.now()); // updated_at

                    stmt.executeUpdate();

                    // Curriculum ID 가져오기
                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            curriculumId = generatedKeys.getLong(1);
                        } else {
                            throw new Exception("Creating curriculum failed, no ID obtained.");
                        }
                    }
                }

                // 4. Tags 삽입
                if (curriculum.getTags() != null && !curriculum.getTags().isEmpty()) {
                    insertTags(connection, curriculumId, curriculum.getTags());
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
                        currentCount, totalCount, curriculum.getStepTitle(), e.getMessage()));
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
    private void insertTags(Connection connection, Long curriculumId, List<String> tags) throws Exception {
        String sql = "INSERT INTO curriculum_tags (curriculum_id, tag) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (String tag : tags) {
                if (tag != null && !tag.trim().isEmpty()) {
                    stmt.setLong(1, curriculumId);
                    stmt.setString(2, truncate(tag, 50));
                    stmt.addBatch();
                }
            }
            stmt.executeBatch();
        }
    }
}
