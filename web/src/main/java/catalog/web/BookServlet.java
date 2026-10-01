package catalog.web;


import catalog.core.domain.Book;
import catalog.core.domain.Page;
import catalog.core.domain.PageRequest;
import catalog.core.exception.ValidationException;
import catalog.core.service.BookService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebServlet(urlPatterns = "/books")
public class BookServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(BookServlet.class);

    private BookService bookService() {
        return (BookService) getServletContext().getAttribute(AppContextListener.BOOK_SERVICE);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String q = req.getParameter("q");
            int page = parseInt(req.getParameter("page"), 0, "page");
            int size = parseInt(req.getParameter("size"), 20, "size");
            String sort = req.getParameter("sort");

            PageRequest pr = new PageRequest(page, size, sort);
            Page<Book> result = bookService().search(q, pr);

            JsonUtil.write(resp, HttpServletResponse.SC_OK, result);
        } catch (ValidationException | IllegalArgumentException e) {
            log.warn("Bad request GET /books: {}", e.getMessage());
            JsonUtil.write(resp, HttpServletResponse.SC_BAD_REQUEST,
                    ErrorResponse.of(400, "Bad Request", e.getMessage(), req.getRequestURI()));
        } catch (Exception e) {
            log.error("Unexpected error in GET /books", e);
            JsonUtil.write(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    ErrorResponse.of(500, "Internal Server Error", "Unexpected error", req.getRequestURI()));
        }
    }

    private int parseInt(String value, int def, String name) {
        if (value == null || value.isBlank()) return def;
        try { return Integer.parseInt(value); }
        catch (NumberFormatException e) { throw new ValidationException(name + " must be integer"); }
    }
}
