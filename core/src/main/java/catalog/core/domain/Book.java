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
        this.id = id;
        this.title = Objects.requireNonNull(title, "title");
        this.author = Objects.requireNonNull(author, "author");
        this.isbn = isbn;
        this.publishedAt = publishedAt;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getIsbn() { return isbn; }
    public Instant getPublishedAt() { return publishedAt; }
}
