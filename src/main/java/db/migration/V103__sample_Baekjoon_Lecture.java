package db.migration;

import com.PBL.lecture.LectureType;
import com.PBL.lecture.dto.TestCaseResponse;
import db.dto.BaekjoonProblemDto;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;

import static db.utile.readJsonFile;

public class V103__sample_Baekjoon_Lecture extends BaseJavaMigration {

    private static int successCount = 0;

    @Override
    public void migrate(Context context) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Connection connection = context.getConnection();

        // 1. 시스템 사용자 생성 또는 가져오기
        // 기본관리자 계정 1번 사용
        Long authorId = 1L; // 기본 관리자 ID 사용 (필요시 수정)

        // 2. JSON 파일 읽기
        List<BaekjoonProblemDto> baekjoon_problems = readJsonFile(
                mapper,
                "db/json/baekjoon/baekjoon_problems.json",
                BaekjoonProblemDto.class);

        // 3. 제약조건 정의
        String constraints_sql = "INSERT INTO submission_constraints (cpu_time_limit,memory_limit) "
                + " VALUES (?, ?)";

        // 4. 문제강의 정의
        String sql = "INSERT INTO Lectures (title, description, "
                + "content ,input_content, output_content,"
                + "type, category, difficulty,"
                + "is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id,  created_at, updated_at)"
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        int total = baekjoon_problems.size();
        int current = 0;
        try (PreparedStatement insert = connection.prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS)) {
            for (BaekjoonProblemDto baekjoon_problem : baekjoon_problems) {
                // 5. 제약조건 처리 (중복 체크)
                Long constraintsId;
                Double timeLimit = baekjoon_problem.getTimeLimit();
                Integer memoryLimit = baekjoon_problem.getMemoryLimit();

                if (timeLimit == null || memoryLimit == null) {
                    System.out
                            .println("Missing time limit or memory limit for problem: " + baekjoon_problem.getTitle());
                    continue;
                }

                String selectConstraintsSql = "SELECT id FROM submission_constraints " +
                        "WHERE cpu_time_limit = ? AND memory_limit = ? LIMIT 1";

                try (PreparedStatement selectStmt = connection.prepareStatement(selectConstraintsSql)) {
                    selectStmt.setDouble(1, timeLimit);
                    selectStmt.setInt(2, memoryLimit);

                    try (ResultSet rs = selectStmt.executeQuery()) {
                        if (rs.next()) {
                            // 기존 제약조건 재사용
                            constraintsId = rs.getLong("id");
                        } else {
                            // 새 제약조건 생성
                            try (PreparedStatement insertStmt = connection.prepareStatement(
                                    constraints_sql, Statement.RETURN_GENERATED_KEYS)) {
                                insertStmt.setDouble(1, timeLimit);
                                insertStmt.setInt(2, memoryLimit);
                                insertStmt.executeUpdate();

                                try (ResultSet keys = insertStmt.getGeneratedKeys()) {
                                    if (keys.next()) {
                                        constraintsId = keys.getLong(1);
                                    } else {
                                        throw new Exception("Creating constraints failed, no ID obtained.");
                                    }
                                }
                            }
                        }
                    }
                }

                // 6. 데이터 추출
                String title = baekjoon_problem.getTitle();
                String description = baekjoon_problem.getDescription();
                String content = baekjoon_problem.getContent();
                String input_content = baekjoon_problem.getInput_content();
                String output_content = baekjoon_problem.getOutput_content();
                LectureType type = LectureType.PROBLEM;
                String category = baekjoon_problem.getCategory();
                String difficulty = baekjoon_problem.getDifficulty();
                Boolean is_public = true;
                String thumbnailImageUrl = null; // NULL 허용
                Integer durationMinutes = 30; // 기본값 30분
                LocalDateTime now = LocalDateTime.now();

                // 7. Lecture 삽입
                insert.setString(1, title);
                insert.setString(2, description);
                insert.setString(3, content);
                insert.setString(4, input_content);
                insert.setString(5, output_content);
                insert.setString(6, type.name());
                insert.setString(7, category);
                insert.setString(8, difficulty);
                insert.setBoolean(9, is_public);
                insert.setString(10, thumbnailImageUrl);
                insert.setInt(11, durationMinutes);
                insert.setLong(12, authorId);
                insert.setLong(13, constraintsId);
                insert.setObject(14, now);
                insert.setObject(15, now);

                insert.executeUpdate();

                // 8. Lecture ID 가져오기
                Long lectureId;
                try (ResultSet generatedKeys = insert.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        lectureId = generatedKeys.getLong(1);
                    } else {
                        throw new Exception("Creating lecture failed, no ID obtained.");
                    }
                }
                // 9. 테스트케이스 추가하기
                String testCaseSql = "INSERT INTO test_cases (input, expected_output, lecture_id, order_index) "
                        + "VALUES (?, ?, ?, ?)";
                try (PreparedStatement testCaseStmt = connection.prepareStatement(testCaseSql)) {
                    for (int i = 0; i < baekjoon_problem.getTestCases().size(); i++) {
                        TestCaseResponse testCase = baekjoon_problem.getTestCases().get(i);
                        testCaseStmt.setString(1, testCase.getInput());
                        testCaseStmt.setString(2, testCase.getExpectedOutput());
                        testCaseStmt.setLong(3, lectureId);
                        testCaseStmt.setInt(4, i + 1);
                        testCaseStmt.addBatch();
                    }
                    testCaseStmt.executeBatch();
                }

                // 10. tags 추가하기
                String tagSql = "INSERT INTO lecture_tags (lecture_id, tag) VALUES (?, ?)";
                try (PreparedStatement tagStmt = connection.prepareStatement(tagSql)) {
                    for (String tag : baekjoon_problem.getTags()) {
                        tagStmt.setLong(1, lectureId);
                        tagStmt.setString(2, tag);
                        tagStmt.addBatch();
                    }
                    tagStmt.executeBatch();
                }
                // 완료 로그

                current++;
                int percent = (current * 100) / total;

                try {
                    // ... 삽입 로직
                    successCount++;

                    // 프로그레스 바 생성
                    int barLength = 50;
                    int filled = (current * barLength) / total;
                    String bar = "█".repeat(filled) + "░".repeat(barLength - filled);

                    System.out.println(String.format("[%s] %d%% (%d/%d) - %s",
                            bar, percent, current, total, baekjoon_problem.getTitle()));

                } catch (Exception e) {
                    System.err.println(
                            String.format("✗ [%d/%d] Failed: %s", current, total, baekjoon_problem.getTitle()));
                }

            }

        }
    }
}