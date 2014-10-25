CREATE TABLE books (id INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL, published_in DATE, status VARCHAR(255) NOT NULL, title VARCHAR(255), author_id INTEGER, language_id INTEGER, PRIMARY KEY (id))
CREATE TABLE languages (id INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL, code VARCHAR(255), name VARCHAR(255), PRIMARY KEY (id))
CREATE TABLE authors (id INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL, date_of_birth DATE, distinguished INTEGER, first_name VARCHAR(255), last_name VARCHAR(255), year_of_birth INTEGER, PRIMARY KEY (id))
CREATE TABLE book_to_bookstore (id INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL, created_at TIMESTAMP, stock INTEGER, updated_at TIMESTAMP, book_id INTEGER, bookstore_id INTEGER, PRIMARY KEY (id))
CREATE TABLE bookstores (id INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL, category SMALLINT, name VARCHAR(255), PRIMARY KEY (id))
CREATE TABLE not_null_example (ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL, A VARCHAR(255), B VARCHAR(255), C VARCHAR(255) NOT NULL, D VARCHAR(255) NOT NULL, PRIMARY KEY (ID))
CREATE TABLE CASEEXAMPLE (ID BIGINT NOT NULL, FOOBAR VARCHAR(255), PRIMARY KEY (ID))
CREATE TABLE enum_example (id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL, as_int INTEGER NOT NULL, as_string VARCHAR(255), PRIMARY KEY (id))
ALTER TABLE books ADD CONSTRAINT books_language_id FOREIGN KEY (language_id) REFERENCES languages (id)
ALTER TABLE books ADD CONSTRAINT FK_books_author_id FOREIGN KEY (author_id) REFERENCES authors (id)
ALTER TABLE book_to_bookstore ADD CONSTRAINT bktobookstorebokid FOREIGN KEY (book_id) REFERENCES books (id) ON DELETE CASCADE
ALTER TABLE book_to_bookstore ADD CONSTRAINT bktbkstorebkstreid FOREIGN KEY (bookstore_id) REFERENCES bookstores (id)
