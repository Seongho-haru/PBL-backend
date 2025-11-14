-- Create books table matching Book entity
CREATE TABLE IF NOT EXISTS books (
    id BIGSERIAL PRIMARY KEY,
    isbn VARCHAR(255) UNIQUE NOT NULL,
    product_id VARCHAR(255) UNIQUE NOT NULL,
    type VARCHAR(255) NOT NULL,
    title VARCHAR(1000) NOT NULL,
    description TEXT,
    language VARCHAR(10),
    publication_date DATE,
    page_count INTEGER,
    duration_seconds INTEGER,
    average_rating DOUBLE PRECISION,
    url VARCHAR(500),
    web_url VARCHAR(500),
    cover_image VARCHAR(500),
    has_sandbox BOOLEAN,
    has_quiz_question_bank BOOLEAN,
    display_format VARCHAR(50),
    marketing_type_name VARCHAR(50),
    ourn VARCHAR(200),
    epub_file_path VARCHAR(1000)
);

-- Create book_authors table for @ElementCollection
CREATE TABLE IF NOT EXISTS book_authors (
    book_id BIGINT NOT NULL,
    author VARCHAR(255),
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
);

-- Create book_publishers table for @ElementCollection
CREATE TABLE IF NOT EXISTS book_publishers (
    book_id BIGINT NOT NULL,
    publisher VARCHAR(255),
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
);

-- Create book_content_levels table for @ElementCollection
CREATE TABLE IF NOT EXISTS book_content_levels (
    book_id BIGINT NOT NULL,
    level VARCHAR(255),
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
);

-- Create book_topics table for @ElementCollection
CREATE TABLE IF NOT EXISTS book_topics (
    book_id BIGINT NOT NULL,
    topic VARCHAR(255),
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
);

-- Create book_categories table for @ElementCollection
CREATE TABLE IF NOT EXISTS book_categories (
    book_id BIGINT NOT NULL,
    category VARCHAR(255),
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
);

-- Create book_tags table for @ElementCollection
CREATE TABLE IF NOT EXISTS book_tags (
    book_id BIGINT NOT NULL,
    tag VARCHAR(255),
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
);

-- Create book_toc table for @ElementCollection with @OrderColumn
CREATE TABLE IF NOT EXISTS book_toc (
    book_id BIGINT NOT NULL,
    toc_item TEXT,
    toc_order INTEGER NOT NULL,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
);

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_books_isbn ON books(isbn);
CREATE INDEX IF NOT EXISTS idx_books_product_id ON books(product_id);
CREATE INDEX IF NOT EXISTS idx_books_title ON books(title);
CREATE INDEX IF NOT EXISTS idx_books_publication_date ON books(publication_date);
CREATE INDEX IF NOT EXISTS idx_book_authors_book_id ON book_authors(book_id);
CREATE INDEX IF NOT EXISTS idx_book_publishers_book_id ON book_publishers(book_id);
CREATE INDEX IF NOT EXISTS idx_book_topics_book_id ON book_topics(book_id);
CREATE INDEX IF NOT EXISTS idx_book_categories_book_id ON book_categories(book_id);
CREATE INDEX IF NOT EXISTS idx_book_toc_book_id ON book_toc(book_id);
