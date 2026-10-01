package catalog.web;


import catalog.core.domain.Book;
import catalog.core.domain.Comment;
import catalog.core.exception.NotFoundException;
import catalog.core.service.BookService;
import catalog.core.service.CommentService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = "/books/*")
public class BookDetailServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(BookDetailServlet.class);

    private BookService bookService() {
        return (BookService) getServletContext().getAttribute(AppContextListener.BOOK_SERVICE);
    }

    private CommentService commentService() {
        return (CommentService) getServletContext().getAttribute(AppContextListener.COMMENT_SERVICE);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                JsonUtil.write(resp, 400,
                        ErrorResponse.of(400, "Bad Request", "Book id required", req.getRequestURI()));
                return;
            }
            long id = Long.parseLong(pathInfo.substring(1));
            Book book = bookService().getById(id);
            List<Comment> comments = commentService().listByBook(id);
            JsonUtil.write(resp, 200, Map.of("book", book, "comments", comments));
        } catch (NumberFormatException e) {
            log.warn("Bad request GET /books/{}: invalid id", req.getPathInfo());
            JsonUtil.write(resp, 400,
                    ErrorResponse.of(400, "Bad Request", "Invalid book id", req.getRequestURI()));
        } catch (NotFoundException e) {
            log.warn("Not found GET {}: {}", req.getRequestURI(), e.getMessage());
            JsonUtil.write(resp, 404,
                    ErrorResponse.of(404, "Not Found", e.getMessage(), req.getRequestURI()));
        } catch (Exception e) {
            log.error("Unexpected error GET /books/{}", req.getPathInfo(), e);
            JsonUtil.write(resp, 500,
                    ErrorResponse.of(500, "Internal Server Error", "Unexpected error", req.getRequestURI()));
        }
    }
}
