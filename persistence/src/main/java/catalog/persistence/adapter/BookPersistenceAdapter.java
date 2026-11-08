package catalog.persistence.adapter;

import catalog.core.domain.Book;
import catalog.core.port.BookPersistencePort;
import catalog.persistence.entity.BookEntity;
import catalog.persistence.repository.BookRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class BookPersistenceAdapter implements BookPersistencePort {

    private final BookRepository repo;

    public BookPersistenceAdapter(BookRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<Book> findAll() {
        return repo.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<Book> findById(Long id) {
        return repo.findById(id).map(this::toDomain);
    }

    @Override
    public Book save(Book book) {
        BookEntity e = new BookEntity(book.getTitle(), book.getAuthor(), book.getYear());
        if (book.getId() != null) e.setId(book.getId());
        return toDomain(repo.save(e));
    }

    @Override
    public void deleteById(Long id) {
        repo.deleteById(id);
    }

    @Override
    public List<Book> searchByTitle(String query) {
        return repo.findByTitleContainingIgnoreCase(query).stream()
                .map(this::toDomain).collect(Collectors.toList());
    }

    private Book toDomain(BookEntity e) {
        return new Book(e.getId(), e.getTitle(), e.getAuthor(), e.getYear());
    }
}