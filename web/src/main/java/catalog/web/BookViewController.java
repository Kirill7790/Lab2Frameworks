package catalog.web;

import catalog.core.domain.Book;
import catalog.core.domain.Page;
import catalog.core.domain.PageRequest;
import catalog.core.exception.ValidationException;
import catalog.core.service.BookService;
import catalog.core.service.CommentService;
import catalog.web.mail.MailService;
import catalog.core.service.UserService;
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
    private final MailService mailService;
    private final UserService userService;

    public BookViewController(BookService bookService,
                              CommentService commentService,
                              MailService mailService, UserService userService) {
        this.bookService = bookService;
        this.commentService = commentService;
        this.mailService = mailService;
        this.userService = userService;
    }

    @GetMapping
    public String list(@RequestParam(name = "q", required = false) String q, Model model) {
        model.addAttribute("books", bookService.search(q));
        return "books";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {
        Book book = bookService.findById(id);
        model.addAttribute("book", book);
        model.addAttribute("comments", commentService.getByBook(id));
        model.addAttribute("users", userService.findAll());
        return "book-detail";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("book", new BookForm());
        return "book-form";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("book") BookForm form) {
        Book saved = bookService.save(form.getTitle(), form.getAuthor(), form.getYear());
        mailService.sendNewBookEmail(saved);
        return "redirect:/books";
    }

    @PostMapping("/{id}/comments")
    public String addComment(@PathVariable("id") Long bookId,
                             @RequestParam("userId") Long userId,
                             @RequestParam("text") String text) {
        try {
            commentService.addComment(bookId, userId, text);
        } catch (Exception e) {
        }

        return "redirect:/books/" + bookId;
    }

    public static class BookForm {
        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public Integer getYear() {
            return year;
        }

        public void setYear(Integer year) {
            this.year = year;
        }

        private String title;
        private String author;
        private Integer year;


    }
}