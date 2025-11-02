package com.PBL.lab.core.service;

import com.PBL.lab.core.entity.Book;
import com.PBL.lab.core.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;

    // ===== 기본 CRUD =====

    @Transactional
    public Book save(Book book) {
        log.info("Saving book: {}", book.getTitle());
        return bookRepository.save(book);
    }

    @Transactional
    public List<Book> saveAll(List<Book> books) {
        log.info("Saving {} books", books.size());
        return bookRepository.saveAll(books);
    }

    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @Transactional
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

    // ===== ISBN/Product ID 검색 =====

    public Optional<Book> findByIsbn(String isbn) {
        log.info("Searching book by ISBN: {}", isbn);
        return bookRepository.findByIsbn(isbn);
    }

    public Optional<Book> findByProductId(String productId) {
        log.info("Searching book by Product ID: {}", productId);
        return bookRepository.findByProductId(productId);
    }

    // ===== 제목/저자/출판사 검색 =====

    public List<Book> searchByTitle(String title) {
        log.info("Searching books by title: {}", title);
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }

    public List<Book> searchByAuthor(String author) {
        log.info("Searching books by author: {}", author);
        return bookRepository.findByAuthorContaining(author);
    }

    public List<Book> searchByPublisher(String publisher) {
        log.info("Searching books by publisher: {}", publisher);
        return bookRepository.findByPublisherContaining(publisher);
    }

    // ===== 카테고리/토픽/태그 검색 =====

    public List<Book> findByCategory(String category) {
        log.info("Searching books by category: {}", category);
        return bookRepository.findByCategory(category);
    }

    public List<Book> findByCategories(List<String> categories) {
        log.info("Searching books by categories: {}", categories);
        List<String> lowerCategories = categories.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());
        return bookRepository.findByCategoriesIn(lowerCategories);
    }

    public List<Book> findByTopic(String topic) {
        log.info("Searching books by topic: {}", topic);
        return bookRepository.findByTopic(topic);
    }

    public List<Book> findByTopics(List<String> topics) {
        log.info("Searching books by topics: {}", topics);
        List<String> lowerTopics = topics.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());
        return bookRepository.findByTopicsIn(lowerTopics);
    }

    public List<Book> findByTag(String tag) {
        log.info("Searching books by tag: {}", tag);
        return bookRepository.findByTag(tag);
    }

    // ===== 난이도 검색 =====

    public List<Book> findByContentLevel(String level) {
        log.info("Searching books by content level: {}", level);
        return bookRepository.findByContentLevel(level);
    }

    // ===== TOC(목차) 검색 - AI RAG에 중요 =====

    public List<Book> searchByToc(String keyword) {
        log.info("Searching books by TOC keyword: {}", keyword);
        return bookRepository.findByTocContaining(keyword);
    }

    // ===== 키워드 검색 =====

    public List<Book> searchByKeyword(String keyword) {
        log.info("Searching books by keyword: {}", keyword);
        return bookRepository.searchByKeyword(keyword);
    }

    public List<Book> fullTextSearch(String keyword) {
        log.info("Full-text search for keyword: {}", keyword);
        return bookRepository.fullTextSearch(keyword);
    }

    // ===== AI 추천을 위한 복합 검색 =====

    public List<Book> findByCategoriesAndLevel(List<String> categories, String level) {
        log.info("Searching books by categories: {} and level: {}", categories, level);
        List<String> lowerCategories = categories.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());
        return bookRepository.findByCategoriesAndLevel(lowerCategories, level);
    }

    public List<Book> findByTopicsAndLevel(List<String> topics, String level) {
        log.info("Searching books by topics: {} and level: {}", topics, level);
        List<String> lowerTopics = topics.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());
        return bookRepository.findByTopicsAndLevel(lowerTopics, level);
    }

    // ===== 평점 기반 검색 =====

    public List<Book> findTopRatedBooks() {
        log.info("Finding top-rated books");
        return bookRepository.findAllOrderByRatingDesc();
    }

    public List<Book> findTopRatedBooksByCategory(String category) {
        log.info("Finding top-rated books in category: {}", category);
        return bookRepository.findByCategory(category);
    }

    // ===== 언어/EPUB 필터링 =====

    public List<Book> findByLanguage(String language) {
        log.info("Searching books by language: {}", language);
        return bookRepository.findByLanguage(language);
    }

    // ===== AI RAG를 위한 스마트 검색 =====

    /**
     * AI가 사용할 통합 검색 메서드
     * 제목, 저자, 설명, TOC를 모두 검색하여 관련 책을 찾음
     */
    public List<Book> smartSearch(String query) {
        log.info("Smart search for query: {}", query);
        return fullTextSearch(query);
    }

    /**
     * AI 추천을 위한 메서드
     * 특정 카테고리와 난이도에 맞는 평점 높은 책 추천
     * Repository에서 이미 평점 순으로 정렬됨
     */
    public List<Book> recommendBooks(List<String> categories, String level, int limit) {
        log.info("Recommending books for categories: {}, level: {}, limit: {}", categories, level, limit);
        List<Book> books = findByCategoriesAndLevel(categories, level);

        return books.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * 목차 기반 관련 책 찾기
     * 특정 주제/챕터를 다루는 책들을 찾을 때 유용
     */
    public List<Book> findRelatedBooksByTopic(String topicKeyword) {
        log.info("Finding books related to topic: {}", topicKeyword);

        // TOC에서 검색
        List<Book> tocResults = searchByToc(topicKeyword);

        // 제목, 설명에서도 검색
        List<Book> keywordResults = searchByKeyword(topicKeyword);

        // 두 결과를 병합하고 중복 제거
        return java.util.stream.Stream.concat(tocResults.stream(), keywordResults.stream())
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 특정 책과 유사한 책 찾기 (같은 카테고리, 비슷한 난이도)
     * Repository에서 이미 평점 순으로 정렬됨
     */
    public List<Book> findSimilarBooks(Long bookId, int limit) {
        log.info("Finding similar books to book ID: {}", bookId);

        Optional<Book> bookOpt = findById(bookId);
        if (bookOpt.isEmpty()) {
            return List.of();
        }

        Book book = bookOpt.get();

        // 같은 카테고리의 책들 중 평점 높은 순으로 반환 (Repository에서 이미 정렬됨)
        if (book.getCategories() != null && !book.getCategories().isEmpty()) {
            return findByCategories(book.getCategories()).stream()
                    .filter(b -> !b.getId().equals(bookId))
                    .limit(limit)
                    .collect(Collectors.toList());
        }

        return List.of();
    }
}
