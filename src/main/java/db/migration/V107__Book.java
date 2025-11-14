package db.migration;

import com.fasterxml.jackson.databind.ObjectMapper;
import db.dto.BookDto;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static db.utile.readJsonFile;

public class V107__Book extends BaseJavaMigration {

    private static int successCount = 0;
    private static int duplicateCount = 0;
    private static int errorCount = 0;

    @Override
    public void migrate(Context context) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Connection connection = context.getConnection();

        // 1. 책 JSON 파일 목록
        String[] bookFiles = {
                "db/json/book_metadata/algorithms_books.json",
                "db/json/book_metadata/programming_books.json"
        };

        // 2. 중복 체크를 위한 Set
        Set<String> processedIsbns = new HashSet<>();

        // 3. 전체 책 수 계산 (진행률 표시용)
        int totalBooks = 0;
        List<List<BookDto>> allBooks = new ArrayList<>();
        for (String bookFile : bookFiles) {
            List<BookDto> books = readJsonFile(mapper, bookFile, BookDto.class);
            allBooks.add(books);
            totalBooks += books.size();
        }

        System.out.println(String.format("\n📚 총 %d개의 책을 로딩합니다...\n", totalBooks));

        int currentCount = 0;

        // 4. 각 JSON 파일 처리
        for (int fileIndex = 0; fileIndex < bookFiles.length; fileIndex++) {
            String bookFile = bookFiles[fileIndex];
            List<BookDto> books = allBooks.get(fileIndex);

            System.out.println(String.format("📖 처리 중: %s (%d개)\n", bookFile, books.size()));

            for (BookDto book : books) {
                // 각 책마다 세이브포인트 생성
                java.sql.Savepoint savepoint = null;
                try {
                    currentCount++;

                    // ISBN이 없거나 이미 처리된 경우 건너뛰기
                    if (book.getIsbn() == null || book.getIsbn().isEmpty()) {
                        System.err.println(String.format("⚠️  [%d/%d] ISBN 없음: %s",
                                currentCount, totalBooks, book.getTitle()));
                        errorCount++;
                        continue;
                    }

                    if (processedIsbns.contains(book.getIsbn())) {
                        duplicateCount++;
                        continue;
                    }

                    // ISBN 중복 체크 (DB에 이미 존재하는지)
                    if (isBookExists(connection, book.getIsbn())) {
                        processedIsbns.add(book.getIsbn());
                        duplicateCount++;
                        continue;
                    }

                    // 세이브포인트 생성
                    savepoint = connection.setSavepoint("book_" + currentCount);

                    // 5. Book 삽입
                    String bookSql = "INSERT INTO books (isbn, product_id, type, title, description, language, " +
                            "publication_date, page_count, duration_seconds, average_rating, url, web_url, " +
                            "cover_image, has_sandbox, has_quiz_question_bank, display_format, " +
                            "marketing_type_name, ourn, epub_file_path) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                    Long bookId;
                    try (PreparedStatement bookStmt = connection.prepareStatement(bookSql,
                            PreparedStatement.RETURN_GENERATED_KEYS)) {

                        bookStmt.setString(1, book.getIsbn());
                        bookStmt.setString(2, book.getProductId() != null ? book.getProductId() : book.getIsbn());
                        bookStmt.setString(3, book.getType() != null ? book.getType() : "book");
                        bookStmt.setString(4, book.getTitle());
                        bookStmt.setString(5, truncate(book.getDescription(), 65535)); // TEXT 타입 최대 크기 제한
                        bookStmt.setString(6, book.getLanguage());

                        // publication_date 파싱
                        if (book.getPublicationDate() != null && !book.getPublicationDate().isEmpty()) {
                            try {
                                LocalDate pubDate = LocalDate.parse(book.getPublicationDate());
                                bookStmt.setObject(7, pubDate);
                            } catch (Exception e) {
                                bookStmt.setObject(7, null);
                            }
                        } else {
                            bookStmt.setObject(7, null);
                        }

                        bookStmt.setObject(8, book.getPageCount());
                        bookStmt.setObject(9, book.getDurationSeconds());
                        bookStmt.setObject(10, book.getAverageRating());
                        bookStmt.setString(11, truncate(book.getUrl(), 500));
                        bookStmt.setString(12, truncate(book.getWebUrl(), 500));
                        bookStmt.setString(13, truncate(book.getCoverImage(), 500));
                        bookStmt.setObject(14, book.getHasSandbox());
                        bookStmt.setObject(15, book.getHasQuizQuestionBank());
                        bookStmt.setString(16, truncate(book.getDisplayFormat(), 50));
                        bookStmt.setString(17, truncate(book.getMarketingTypeName(), 50));
                        bookStmt.setString(18, truncate(book.getOurn(), 200));
                        bookStmt.setString(19, truncate(book.getEpubFilePath(), 1000));

                        bookStmt.executeUpdate();

                        // Book ID 가져오기
                        try (ResultSet generatedKeys = bookStmt.getGeneratedKeys()) {
                            if (generatedKeys.next()) {
                                bookId = generatedKeys.getLong(1);
                            } else {
                                throw new Exception("Creating book failed, no ID obtained.");
                            }
                        }
                    }

                    // 6. Authors 삽입
                    if (book.getAuthors() != null && !book.getAuthors().isEmpty()) {
                        insertCollection(connection, "book_authors", "book_id", "author", bookId, book.getAuthors());
                    }

                    // 7. Publishers 삽입
                    if (book.getPublishers() != null && !book.getPublishers().isEmpty()) {
                        insertCollection(connection, "book_publishers", "book_id", "publisher", bookId, book.getPublishers());
                    }

                    // 8. Content Levels 삽입
                    if (book.getContentLevels() != null && !book.getContentLevels().isEmpty()) {
                        insertCollection(connection, "book_content_levels", "book_id", "level", bookId, book.getContentLevels());
                    }

                    // 9. Topics 삽입
                    if (book.getTopics() != null && !book.getTopics().isEmpty()) {
                        insertCollection(connection, "book_topics", "book_id", "topic", bookId, book.getTopics());
                    }

                    // 10. Categories 삽입 (topic_hierarchy에서 추출)
                    if (book.getTopicHierarchy() != null && !book.getTopicHierarchy().isEmpty()) {
                        List<String> categories = new ArrayList<>();
                        for (var topicMap : book.getTopicHierarchy()) {
                            if (topicMap.get("name") != null) {
                                categories.add(topicMap.get("name").toString());
                            }
                        }
                        if (!categories.isEmpty()) {
                            insertCollection(connection, "book_categories", "book_id", "category", bookId, categories);
                        }
                    }

                    // 11. TOC 삽입 (순서 유지)
                    if (book.getToc() != null && !book.getToc().isEmpty()) {
                        insertToc(connection, bookId, book.getToc());
                    }

                    // 세이브포인트 해제 (성공)
                    if (savepoint != null) {
                        connection.releaseSavepoint(savepoint);
                    }

                    processedIsbns.add(book.getIsbn());
                    successCount++;

                    // 진행률 표시 (10%마다)
                    if (currentCount % Math.max(1, totalBooks / 10) == 0 || currentCount == totalBooks) {
                        int percent = (currentCount * 100) / totalBooks;
                        int barLength = 50;
                        int filled = (currentCount * barLength) / totalBooks;
                        String bar = "█".repeat(filled) + "░".repeat(barLength - filled);

                        System.out.println(String.format("[%s] %d%% (%d/%d) - 성공: %d, 중복: %d, 오류: %d",
                                bar, percent, currentCount, totalBooks, successCount, duplicateCount, errorCount));
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
                            currentCount, totalBooks, book.getTitle(), e.getMessage()));
                }
            }
        }

        System.out.println(String.format("\n✓ 완료: 성공 %d개, 중복 %d개, 오류 %d개, 총 %d개 처리",
                successCount, duplicateCount, errorCount, totalBooks));
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
     * ISBN으로 책이 이미 존재하는지 확인
     */
    private boolean isBookExists(Connection connection, String isbn) throws Exception {
        String sql = "SELECT COUNT(*) FROM books WHERE isbn = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, isbn);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * 컬렉션 데이터 삽입 (authors, publishers, topics 등)
     */
    private void insertCollection(Connection connection, String tableName, String idColumn,
                                   String valueColumn, Long id, List<String> values) throws Exception {
        String sql = String.format("INSERT INTO %s (%s, %s) VALUES (?, ?)", tableName, idColumn, valueColumn);
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (String value : values) {
                if (value != null && !value.trim().isEmpty()) {
                    stmt.setLong(1, id);
                    stmt.setString(2, value);
                    stmt.addBatch();
                }
            }
            stmt.executeBatch();
        }
    }

    /**
     * TOC 삽입 (순서 유지)
     */
    private void insertToc(Connection connection, Long bookId, List<String> tocItems) throws Exception {
        String sql = "INSERT INTO book_toc (book_id, toc_item, toc_order) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (int i = 0; i < tocItems.size(); i++) {
                String tocItem = tocItems.get(i);
                if (tocItem != null && !tocItem.trim().isEmpty()) {
                    stmt.setLong(1, bookId);
                    stmt.setString(2, tocItem);
                    stmt.setInt(3, i);
                    stmt.addBatch();
                }
            }
            stmt.executeBatch();
        }
    }
}
