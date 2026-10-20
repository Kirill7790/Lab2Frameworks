package catalog.core.domain;

import java.time.Instant;
import java.util.Objects;

public class Comment {
    private final Long id;
    private final Long bookId;
    private final String author;
    private final String text;
    private final Instant createdAt;

    public Comment(Long id, Long bookId, String author, String text, Instant createdAt) {
        if (bookId == null || bookId <= 0) {
            throw new IllegalArgumentException("Comment.bookId must be positive");
        }
        if (author == null || author.isBlank()) {
            throw new IllegalArgumentException("Comment.author must not be blank");
        }
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Comment.text must not be blank");
        }
        if (author.length() > 255) {
            throw new IllegalArgumentException("Comment.author too long (max 255)");
        }
        if (text.length() > 2000) {
            throw new IllegalArgumentException("Comment.text too long (max 2000)");
        }
        this.id = id;
        this.bookId = bookId;
        this.author = author.trim();
        this.text = text.trim();
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }

    public Long getId() { return id; }
    public Long getBookId() { return bookId; }
    public String getAuthor() { return author; }
    public String getText() { return text; }
    public Instant getCreatedAt() { return createdAt; }
}