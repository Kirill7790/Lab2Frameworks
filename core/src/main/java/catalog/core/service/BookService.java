package catalog.core.service;


import catalog.core.domain.Book;
import catalog.core.domain.Page;
import catalog.core.domain.PageRequest;
import catalog.core.exception.NotFoundException;
import catalog.core.port.CatalogRepositoryPort;
import org.springframework.stereotype.Service;

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
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Book " + id + " not found"));
    }
}
