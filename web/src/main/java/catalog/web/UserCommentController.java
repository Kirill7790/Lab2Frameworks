package catalog.web;

import catalog.core.service.CommentService;
import catalog.core.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class UserCommentController {

    private final UserService userService;
    private final CommentService commentService;

    public UserCommentController(UserService userService, CommentService commentService) {
        this.userService = userService;
        this.commentService = commentService;
    }

    @GetMapping("/users/{id}/comments")
    public String userComments(@PathVariable("id") Long id, Model model) {
        model.addAttribute("comments", commentService.getByUser(id));
        model.addAttribute("username", userService.findById(id).getUsername());
        return "user-comments";
    }
}