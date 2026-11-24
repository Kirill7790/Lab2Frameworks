package catalog.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorPageController {

    @GetMapping("/403.html")
    public String forbidden() {
        return "error/403.html";
    }

    @GetMapping("/404")
    public String notFound() {
        return "error/404";
    }
}