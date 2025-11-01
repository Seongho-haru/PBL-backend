package com.PBL.ai.tools;

import com.PBL.lab.core.entity.Book;
import com.PBL.lab.core.service.BookService;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * AI가 사용할 도서 검색 및 추천 도구
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BookTools {

    private final BookService bookService;

    @Tool("프로그래밍 도서를 다양한 조건으로 통합 검색합니다. " +
          "키워드로 제목/저자/목차/설명을 전체 검색하거나, ISBN/ProductID로 정확한 도서를 찾거나, " +
          "카테고리(예: Algorithms, Python)나 토픽(예: Machine Learning), 난이도(beginner/intermediate/advanced)로 필터링할 수 있습니다. " +
          "모든 결과는 평점 높은 순으로 자동 정렬됩니다. 검색하지 않을 조건은 null이나 빈 문자열로 전달하세요.")
    public Map<String, Object> searchBooks(
            @P("전체 텍스트 검색 키워드 (제목, 저자, 목차, 설명에서 검색, 선택)") String keyword,
            @P("ISBN 번호 (정확한 도서 검색, 선택)") String isbn,
            @P("카테고리 (예: Algorithms, Python, 선택)") String category,
            @P("토픽 (예: Machine Learning, Web Development, 선택)") String topic,
            @P("난이도 (beginner/intermediate/advanced, 선택)") String level,
            @P("저자명 (부분 일치, 선택)") String author,
            @P("반환할 최대 결과 수 (기본 10개)") Integer limit) {

        log.debug("🔧 [도구 호출] searchBooks - 키워드: {}, ISBN: {}, 카테고리: {}, 토픽: {}, 난이도: {}, 저자: {}",
                  keyword, isbn, category, topic, level, author);

        Map<String, Object> result = new HashMap<>();
        List<Book> books = null;
        int resultLimit = (limit != null && limit > 0) ? limit : 10;

        try {
            // 1. ISBN으로 정확한 검색 (우선순위 최고)
            if (isbn != null && !isbn.trim().isEmpty()) {
                bookService.findByIsbn(isbn).ifPresent(book -> {
                    result.put("books", List.of(book));
                    result.put("search_type", "isbn");
                    result.put("total_count", 1);
                });
                if (!result.isEmpty()) {
                    log.debug("✅ [도구 결과] searchBooks - ISBN 검색 성공");
                    return result;
                }
            }

            // 2. 복합 검색: 카테고리 + 난이도
            if (category != null && !category.trim().isEmpty() &&
                level != null && !level.trim().isEmpty()) {
                books = bookService.findByCategoriesAndLevel(List.of(category), level);
                result.put("search_type", "category_and_level");
            }
            // 3. 토픽 + 난이도
            else if (topic != null && !topic.trim().isEmpty() &&
                     level != null && !level.trim().isEmpty()) {
                books = bookService.findByTopicsAndLevel(List.of(topic), level);
                result.put("search_type", "topic_and_level");
            }
            // 4. 카테고리만
            else if (category != null && !category.trim().isEmpty()) {
                books = bookService.findByCategory(category);
                result.put("search_type", "category");
            }
            // 5. 토픽만
            else if (topic != null && !topic.trim().isEmpty()) {
                books = bookService.findByTopic(topic);
                result.put("search_type", "topic");
            }
            // 6. 난이도만
            else if (level != null && !level.trim().isEmpty()) {
                books = bookService.findByContentLevel(level);
                result.put("search_type", "level");
            }
            // 7. 저자 검색
            else if (author != null && !author.trim().isEmpty()) {
                books = bookService.searchByAuthor(author);
                result.put("search_type", "author");
            }
            // 8. 키워드 전체 검색 (제목, 저자, 설명, TOC)
            else if (keyword != null && !keyword.trim().isEmpty()) {
                books = bookService.fullTextSearch(keyword);
                result.put("search_type", "full_text");
            }
            // 9. 조건이 없으면 평점 높은 책 추천
            else {
                books = bookService.findTopRatedBooks();
                result.put("search_type", "top_rated");
            }

            // 결과 제한 및 반환
            if (books != null) {
                List<Book> limitedBooks = books.stream()
                        .limit(resultLimit)
                        .collect(Collectors.toList());

                result.put("books", limitedBooks);
                result.put("total_count", books.size());
                result.put("returned_count", limitedBooks.size());
                result.put("sorted_by", "rating_desc");
            }

            log.debug("✅ [도구 결과] searchBooks - 검색 타입: {}, 결과: {}개",
                      result.get("search_type"), result.get("returned_count"));

        } catch (Exception e) {
            log.error("❌ [도구 오류] searchBooks - 에러: {}", e.getMessage());
            result.put("error", e.getMessage());
            result.put("books", List.of());
        }

        return result;
    }

    @Tool("특정 프로그래밍 주제나 기술을 다루는 도서를 목차(TOC) 기반으로 검색합니다. " +
          "예를 들어 'Dynamic Programming', 'Neural Networks', 'REST API' 같은 주제를 입력하면 " +
          "해당 주제를 다루는 챕터가 있는 책들을 찾아줍니다. 학습할 특정 주제에 대한 책을 찾을 때 유용합니다. " +
          "평점 높은 순으로 정렬되어 반환됩니다.")
    public Map<String, Object> searchBooksByTopic(
            @P("검색할 주제/기술 키워드 (예: Dynamic Programming, Docker, React)") String topicKeyword,
            @P("반환할 최대 결과 수 (기본 5개)") Integer limit) {

        log.debug("🔧 [도구 호출] searchBooksByTopic - 주제: {}, 제한: {}", topicKeyword, limit);

        Map<String, Object> result = new HashMap<>();
        int resultLimit = (limit != null && limit > 0) ? limit : 5;

        try {
            // TOC와 키워드 검색 결과를 병합 (중복 제거)
            List<Book> books = bookService.findRelatedBooksByTopic(topicKeyword);

            List<Book> limitedBooks = books.stream()
                    .limit(resultLimit)
                    .collect(Collectors.toList());

            result.put("books", limitedBooks);
            result.put("topic", topicKeyword);
            result.put("total_found", books.size());
            result.put("returned_count", limitedBooks.size());
            result.put("search_areas", "TOC, title, description, authors");
            result.put("sorted_by", "rating_desc");

            log.debug("✅ [도구 결과] searchBooksByTopic - 주제: {}, 결과: {}개", topicKeyword, limitedBooks.size());

        } catch (Exception e) {
            log.error("❌ [도구 오류] searchBooksByTopic - 에러: {}", e.getMessage());
            result.put("error", e.getMessage());
            result.put("books", List.of());
        }

        return result;
    }

    @Tool("카테고리와 난이도에 맞는 프로그래밍 도서를 추천합니다. " +
          "학습자의 수준(beginner/intermediate/advanced)과 관심 분야(예: Algorithms, Python, Web Development)에 맞는 " +
          "평점 높은 책들을 추천합니다. 여러 카테고리를 쉼표로 구분하여 입력할 수 있습니다.")
    public Map<String, Object> recommendBooks(
            @P("카테고리 목록 (쉼표로 구분, 예: Algorithms,Data Structures)") String categories,
            @P("난이도 (beginner/intermediate/advanced)") String level,
            @P("추천할 도서 수 (기본 5개)") Integer limit) {

        log.debug("🔧 [도구 호출] recommendBooks - 카테고리: {}, 난이도: {}, 제한: {}", categories, level, limit);

        Map<String, Object> result = new HashMap<>();
        int resultLimit = (limit != null && limit > 0) ? limit : 5;

        try {
            List<String> categoryList = categories != null && !categories.trim().isEmpty()
                    ? List.of(categories.split(","))
                    : List.of();

            List<Book> books = bookService.recommendBooks(categoryList, level, resultLimit);

            result.put("books", books);
            result.put("categories", categoryList);
            result.put("level", level);
            result.put("count", books.size());
            result.put("sorted_by", "rating_desc");
            result.put("recommendation_type", "personalized");

            log.debug("✅ [도구 결과] recommendBooks - 추천 개수: {}", books.size());

        } catch (Exception e) {
            log.error("❌ [도구 오류] recommendBooks - 에러: {}", e.getMessage());
            result.put("error", e.getMessage());
            result.put("books", List.of());
        }

        return result;
    }

    @Tool("특정 도서와 유사한 다른 도서들을 추천합니다. " +
          "같은 카테고리의 책들 중 평점이 높은 책들을 찾아줍니다. " +
          "현재 읽고 있는 책과 비슷한 다른 책을 찾을 때 유용합니다.")
    public Map<String, Object> findSimilarBooks(
            @P("기준 도서 ID") Long bookId,
            @P("추천할 유사 도서 수 (기본 5개)") Integer limit) {

        log.debug("🔧 [도구 호출] findSimilarBooks - 도서 ID: {}, 제한: {}", bookId, limit);

        Map<String, Object> result = new HashMap<>();
        int resultLimit = (limit != null && limit > 0) ? limit : 5;

        try {
            Book originalBook = bookService.findById(bookId)
                    .orElseThrow(() -> new IllegalArgumentException("도서를 찾을 수 없습니다: " + bookId));

            List<Book> similarBooks = bookService.findSimilarBooks(bookId, resultLimit);

            result.put("original_book", Map.of(
                    "id", originalBook.getId(),
                    "title", originalBook.getTitle(),
                    "categories", originalBook.getCategories() != null ? originalBook.getCategories() : List.of()
            ));
            result.put("similar_books", similarBooks);
            result.put("count", similarBooks.size());
            result.put("sorted_by", "rating_desc");
            result.put("matching_criteria", "same_categories");

            log.debug("✅ [도구 결과] findSimilarBooks - 유사 도서: {}개", similarBooks.size());

        } catch (Exception e) {
            log.error("❌ [도구 오류] findSimilarBooks - 에러: {}", e.getMessage());
            result.put("error", e.getMessage());
            result.put("similar_books", List.of());
        }

        return result;
    }
}
