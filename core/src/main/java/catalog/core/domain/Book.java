package catalog.core.domain;

import java.time.Instant;
import java.util.Objects;

public class Book {
    private final Long id;
    private final String title;
    private final String author;
    private final String isbn;
    private final Instant publishedAt;

    public Book(Long id, String title, String author, String isbn, Instant publishedAt) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Book.title must not be blank");
        }
        if (author == null || author.isBlank()) {
            throw new IllegalArgumentException("Book.author must not be blank");
        }
        if (title.length() > 255 || author.length() > 255) {
            throw new IllegalArgumentException("Book.title/author too long (max 255)");
        }
        if (isbn != null && isbn.length() > 32) {
            throw new IllegalArgumentException("Book.isbn too long (max 32)");
        }
        this.id = id;
        this.title = title.trim();
        this.author = author.trim();
        this.isbn = isbn;
        this.publishedAt = publishedAt;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getIsbn() { return isbn; }
    public Instant getPublishedAt() { return publishedAt; }
}