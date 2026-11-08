package catalog.core.port;

import catalog.core.domain.Book;
import java.util.List;
import java.util.Optional;

public interface BookPersistencePort {
    List<Book> findAll();
    Optional<Book> findById(Long id);
    Book save(Book book);
    void deleteById(Long id);
    List<Book> searchByTitle(String query);
}