package catalog.web;

import catalog.core.service.CommentService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Instant;

@Controller
public class CommentsController {

    private final CommentService commentService;

    public CommentsController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/comments/delete")
    public String delete(@RequestParam("bookId") long bookId,
                         @RequestParam("commentId") long commentId,
                         @RequestParam("createdAt") String createdAt) {
        Instant instant = Instant.parse(createdAt);
        commentService.delete(bookId, commentId, instant);
        return "redirect:/books/" + bookId;
    }
}