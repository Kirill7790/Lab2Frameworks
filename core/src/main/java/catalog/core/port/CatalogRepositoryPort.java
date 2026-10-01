package catalog.core.port;


import catalog.core.domain.Book;
import catalog.core.domain.Page;
import catalog.core.domain.PageRequest;
import java.util.Optional;

public interface CatalogRepositoryPort {
    Page<Book> findBooks(String query, PageRequest request);
    Optional<Book> findById(long id);
}
