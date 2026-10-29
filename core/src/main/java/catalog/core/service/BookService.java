package catalog.core.service;

import catalog.core.domain.Book;
import catalog.core.domain.Page;
import catalog.core.domain.PageRequest;
import catalog.core.exception.NotFoundException;
import catalog.core.exception.ValidationException;
import catalog.core.port.CatalogRepositoryPort;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class BookService {
    private final CatalogRepositoryPort repository;

    public BookService(CatalogRepositoryPort repository) {
        this.repository = repository;
    }

    public Page<Book> search(String query, PageRequest request) {
        return repository.findBooks(query == null ? "" : query.trim(), request);
    }

    public Book getById(long id) {
        if (id <= 0) {
            throw new ValidationException("id must be positive");
        }
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Book " + id + " not found"));
    }

    public Book addBook(String title, String author, String isbn) {
        if (title == null || title.isBlank()) {
            throw new ValidationException("Title is required");
        }
        if (author == null || author.isBlank()) {
            throw new ValidationException("Author is required");
        }
        Book book = new Book(null, title.trim(), author.trim(),
                (isbn == null || isbn.isBlank()) ? null : isbn.trim(),
                Instant.now());
        return repository.save(book);
    }
}