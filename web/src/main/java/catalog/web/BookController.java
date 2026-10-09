package catalog.web;

import catalog.core.domain.Book;
import catalog.core.domain.Comment;
import catalog.core.domain.Page;
import catalog.core.domain.PageRequest;
import catalog.core.service.BookService;
import catalog.core.service.CommentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;
    private final CommentService commentService;

    // Кастомний параметр із application.yaml
    @Value("${catalog.page.default-size}")
    private int defaultPageSize;

    // Конструкторна ін'єкція — рекомендований спосіб
    public BookController(BookService bookService, CommentService commentService) {
        this.bookService = bookService;
        this.commentService = commentService;
    }

    @GetMapping
    public Page<Book> list(
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", required = false) Integer size,
            @RequestParam(name = "sort", defaultValue = "title,asc") String sort) {

        int actualSize = (size == null) ? defaultPageSize : size;
        PageRequest pr = new PageRequest(page, actualSize, sort);
        return bookService.search(q, pr);
    }

    @GetMapping("/{id}")
    public Map<String, Object> detail(@PathVariable("id") long id) {
        Book book = bookService.getById(id);
        List<Comment> comments = commentService.listByBook(id);
        return Map.of("book", book, "comments", comments);
    }

    @PostMapping("/{id}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public Comment addComment(@PathVariable("id") long id,
                              @RequestBody CommentRequest req) {
        return commentService.addComment(id, req.author(), req.text());
    }

    // DTO для POST-запиту
    public record CommentRequest(String author, String text) {}
}