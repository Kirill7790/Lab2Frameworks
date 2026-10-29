package catalog.web;

import catalog.core.domain.Book;
import catalog.core.domain.Page;
import catalog.core.domain.PageRequest;
import catalog.core.exception.ValidationException;
import catalog.core.service.BookService;
import catalog.core.service.CommentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/books")
public class BookViewController {

    private final BookService bookService;
    private final CommentService commentService;

    @Value("${catalog.page.default-size:20}")
    private int defaultPageSize;

    public BookViewController(BookService bookService, CommentService commentService) {
        this.bookService = bookService;
        this.commentService = commentService;
    }

    /**
     * GET /books — список книг.
     * Повертає шаблон "books" (→ templates/books.html).
     */
    @GetMapping
    public String list(@RequestParam(name = "q", required = false) String q,
                       Model model) {
        Page<Book> page = bookService.search(q, new PageRequest(0, defaultPageSize, "title,asc"));
        model.addAttribute("books", page.getContent());
        model.addAttribute("query", q);
        return "books";                     // ← ім'я шаблону
    }

    /**
     * GET /books/{id} — сторінка книги з коментарями.
     * Повертає шаблон "book-detail".
     */
    @GetMapping("/{id}")
    public String detail(@PathVariable("id") long id, Model model) {
        Book book = bookService.getById(id);
        model.addAttribute("book", book);
        model.addAttribute("comments", commentService.listByBook(id));
        return "book-detail";
    }

    /**
     * GET /books/add — показати форму.
     */
    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("book", new BookForm());
        return "book-form";
    }


    @PostMapping("/add")
    public String add(@ModelAttribute("book") BookForm form, Model model) {
        try {
            bookService.addBook(form.getTitle(), form.getAuthor(), form.getIsbn());
            return "redirect:/books";
        } catch (ValidationException e) {
            model.addAttribute("error", e.getMessage());
            return "book-form";
        }
    }

    public static class BookForm {
        private String title;
        private String author;
        private String isbn;

        public String getTitle()
        {
            return title;
        }

        public void setTitle(String title)
        {
            this.title = title;
        }

        public String getAuthor()
        {
            return author;
        }

        public void setAuthor(String author)
        {
            this.author = author;
        }

        public String getIsbn()
        {
            return isbn;
        }

        public void setIsbn(String isbn)
        {
            this.isbn = isbn;
        }
    }
}