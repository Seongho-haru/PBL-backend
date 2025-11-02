package com.PBL.lab.core.repository;

import com.PBL.lab.core.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // ISBN으로 검색
    Optional<Book> findByIsbn(String isbn);

    // Product ID로 검색
    Optional<Book> findByProductId(String productId);

    // 제목으로 검색 (평점 높은 순 정렬)
    @Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%')) " +
           "ORDER BY b.averageRating DESC NULLS LAST")
    List<Book> findByTitleContainingIgnoreCase(@Param("title") String title);

    // 저자로 검색 (평점 높은 순 정렬)
    @Query("SELECT b FROM Book b JOIN b.authors a WHERE LOWER(a) LIKE LOWER(CONCAT('%', :author, '%')) " +
           "ORDER BY b.averageRating DESC NULLS LAST")
    List<Book> findByAuthorContaining(@Param("author") String author);

    // 출판사로 검색 (평점 높은 순 정렬)
    @Query("SELECT b FROM Book b JOIN b.publishers p WHERE LOWER(p) LIKE LOWER(CONCAT('%', :publisher, '%')) " +
           "ORDER BY b.averageRating DESC NULLS LAST")
    List<Book> findByPublisherContaining(@Param("publisher") String publisher);

    // 카테고리로 검색 (평점 높은 순 정렬)
    @Query("SELECT b FROM Book b JOIN b.categories c WHERE LOWER(c) = LOWER(:category) " +
           "ORDER BY b.averageRating DESC NULLS LAST")
    List<Book> findByCategory(@Param("category") String category);

    // 여러 카테고리로 검색 (평점 높은 순 정렬)
    @Query("SELECT DISTINCT b FROM Book b JOIN b.categories c WHERE LOWER(c) IN :categories " +
           "ORDER BY b.averageRating DESC NULLS LAST")
    List<Book> findByCategoriesIn(@Param("categories") List<String> categories);

    // 토픽으로 검색 (평점 높은 순 정렬)
    @Query("SELECT b FROM Book b JOIN b.topics t WHERE LOWER(t) = LOWER(:topic) " +
           "ORDER BY b.averageRating DESC NULLS LAST")
    List<Book> findByTopic(@Param("topic") String topic);

    // 여러 토픽으로 검색 (평점 높은 순 정렬)
    @Query("SELECT DISTINCT b FROM Book b JOIN b.topics t WHERE LOWER(t) IN :topics " +
           "ORDER BY b.averageRating DESC NULLS LAST")
    List<Book> findByTopicsIn(@Param("topics") List<String> topics);

    // 태그로 검색 (평점 높은 순 정렬)
    @Query("SELECT b FROM Book b JOIN b.tags tag WHERE LOWER(tag) = LOWER(:tag) " +
           "ORDER BY b.averageRating DESC NULLS LAST")
    List<Book> findByTag(@Param("tag") String tag);

    // 난이도로 검색 (평점 높은 순 정렬)
    @Query("SELECT b FROM Book b JOIN b.contentLevels cl WHERE LOWER(cl) = LOWER(:level) " +
           "ORDER BY b.averageRating DESC NULLS LAST")
    List<Book> findByContentLevel(@Param("level") String level);

    // TOC(목차)에서 키워드 검색 (평점 높은 순 정렬)
    @Query("SELECT DISTINCT b FROM Book b JOIN b.toc t WHERE LOWER(t) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "ORDER BY b.averageRating DESC NULLS LAST")
    List<Book> findByTocContaining(@Param("keyword") String keyword);

    // 복합 검색: 제목, 저자, 설명에서 키워드 검색 (평점 높은 순 정렬)
    @Query("SELECT DISTINCT b FROM Book b LEFT JOIN b.authors a " +
           "WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(b.description) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(a) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "ORDER BY b.averageRating DESC NULLS LAST")
    List<Book> searchByKeyword(@Param("keyword") String keyword);

    // AI 추천을 위한 복합 검색: 카테고리 + 난이도 (평점 높은 순 정렬)
    @Query("SELECT DISTINCT b FROM Book b " +
           "JOIN b.categories c " +
           "JOIN b.contentLevels cl " +
           "WHERE LOWER(c) IN :categories " +
           "AND LOWER(cl) = LOWER(:level) " +
           "ORDER BY b.averageRating DESC NULLS LAST")
    List<Book> findByCategoriesAndLevel(@Param("categories") List<String> categories,
                                        @Param("level") String level);

    // AI 추천을 위한 복합 검색: 토픽 + 난이도 (평점 높은 순 정렬)
    @Query("SELECT DISTINCT b FROM Book b " +
           "JOIN b.topics t " +
           "JOIN b.contentLevels cl " +
           "WHERE LOWER(t) IN :topics " +
           "AND LOWER(cl) = LOWER(:level) " +
           "ORDER BY b.averageRating DESC NULLS LAST")
    List<Book> findByTopicsAndLevel(@Param("topics") List<String> topics,
                                     @Param("level") String level);

    // 평점 기준으로 정렬하여 검색
    @Query("SELECT b FROM Book b ORDER BY b.averageRating DESC NULLS LAST")
    List<Book> findAllOrderByRatingDesc();

    // 언어로 필터링 (평점 높은 순 정렬)
    @Query("SELECT b FROM Book b WHERE b.language = :language " +
           "ORDER BY b.averageRating DESC NULLS LAST")
    List<Book> findByLanguage(@Param("language") String language);

    // 전체 텍스트 검색 (제목, 설명, TOC, 저자 모두 포함, 평점 높은 순 정렬)
    @Query("SELECT DISTINCT b FROM Book b " +
           "LEFT JOIN b.authors a " +
           "LEFT JOIN b.toc t " +
           "WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(b.description) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(a) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(t) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "ORDER BY b.averageRating DESC NULLS LAST")
    List<Book> fullTextSearch(@Param("keyword") String keyword);
}
