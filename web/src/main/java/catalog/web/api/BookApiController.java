package catalog.web.api;

import catalog.core.domain.Book;
import catalog.core.service.BookService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookApiController {

    private final BookService bookService;

    public BookApiController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public List<Book> list() {
        return bookService.findAll();
    }

    @GetMapping("/{id}")
    public Book get(@PathVariable("id") Long id) {
        return bookService.findById(id);
    }

    @PostMapping
    public Book create(@RequestBody BookRequest req) {
        return bookService.save(req.title(), req.author(), req.year());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        bookService.delete(id);
    }

    public record BookRequest(String title, String author, Integer year) {}
}