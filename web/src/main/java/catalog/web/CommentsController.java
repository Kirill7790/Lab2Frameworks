package catalog.web;

import catalog.core.domain.Comment;
import catalog.core.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentsController {

    private final CommentService commentService;

    public CommentsController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public List<Comment> listAll() {
        return commentService.listAll();
    }

    @GetMapping("/{id}")
    public Comment getOne(@PathVariable("id") long id) {
        return commentService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Comment add(@RequestBody CommentRequest req) {
        return commentService.addComment(req.bookId(), req.author(), req.text());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") long id) {
        commentService.deleteComment(id);
    }

    public record CommentRequest(long bookId, String author, String text) {}
}