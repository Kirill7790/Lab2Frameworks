package catalog.core.domain;

import java.time.LocalDateTime;

public class Comment {
    private final Long id;
    private final String text;
    private final LocalDateTime createdAt;
    private final Long bookId;
    private final Long userId;
    private final String username;

    public Comment(Long id, String text, LocalDateTime createdAt,
                   Long bookId, Long userId, String username) {
        this.id = id;
        this.text = text;
        this.createdAt = createdAt;
        this.bookId = bookId;
        this.userId = userId;
        this.username = username;
    }
    public Long getId() { return id; }
    public String getText() { return text; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Long getBookId() { return bookId; }
    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
}