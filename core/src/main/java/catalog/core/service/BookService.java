package catalog.core.service;

import catalog.core.domain.Book;
import catalog.core.exception.NotFoundException;
import catalog.core.exception.ValidationException;
import catalog.core.port.BookPersistencePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookService {
    private final BookPersistencePort port;

    public BookService(BookPersistencePort port) {
        this.port = port;
    }

    public List<Book> findAll() {
        return port.findAll();
    }

    public Book findById(Long id) {
        return port.findById(id)
                .orElseThrow(() -> new NotFoundException("Book " + id + " not found"));
    }

    @Transactional
    public Book save(String title, String author, Integer year) {
        if (title == null || title.isBlank()) {
            throw new ValidationException("Title is required");
        }
        if (author == null || author.isBlank()) {
            throw new ValidationException("Author is required");
        }
        return port.save(new Book(null, title.trim(), author.trim(), year));
    }

    @Transactional
    public void delete(Long id) {
        port.deleteById(id);
    }

    public List<Book> search(String query) {
        return (query == null || query.isBlank())
                ? port.findAll()
                : port.searchByTitle(query);
    }
}