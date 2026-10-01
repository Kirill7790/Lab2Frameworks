package catalog.web;

import catalog.core.domain.Comment;
import catalog.core.exception.ConflictException;
import catalog.core.exception.NotFoundException;
import catalog.core.exception.ValidationException;
import catalog.core.service.CommentService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

@WebServlet(urlPatterns = "/comments/*")
public class CommentServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(CommentServlet.class);

    private CommentService service() {
        return (CommentService) getServletContext().getAttribute(AppContextListener.COMMENT_SERVICE);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Map<?, ?> body = JsonUtil.mapper().readValue(req.getInputStream(), Map.class);
            Object bookIdRaw = body.get("bookId");
            if (bookIdRaw == null) {
                throw new ValidationException("bookId is required");
            }
            long bookId = Long.parseLong(bookIdRaw.toString());
            String author = (String) body.get("author");
            String text = (String) body.get("text");

            Comment created = service().addComment(bookId, author, text);
            JsonUtil.write(resp, 201, created);
        } catch (ValidationException | NumberFormatException
                 | com.fasterxml.jackson.core.JacksonException e) {
            log.warn("Bad request POST /comments: {}", e.getMessage());
            JsonUtil.write(resp, 400,
                    ErrorResponse.of(400, "Bad Request", e.getMessage(), req.getRequestURI()));
        } catch (NotFoundException e) {
            log.warn("Not found POST /comments: {}", e.getMessage());
            JsonUtil.write(resp, 404,
                    ErrorResponse.of(404, "Not Found", e.getMessage(), req.getRequestURI()));
        } catch (Exception e) {
            log.error("Unexpected error POST /comments", e);
            JsonUtil.write(resp, 500,
                    ErrorResponse.of(500, "Internal Server Error", "Unexpected error", req.getRequestURI()));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String path = req.getPathInfo();
            if (path == null || path.equals("/")) {
                JsonUtil.write(resp, 400,
                        ErrorResponse.of(400, "Bad Request", "Comment id required", req.getRequestURI()));
                return;
            }
            long commentId = Long.parseLong(path.substring(1));
            service().deleteComment(commentId);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (NumberFormatException e) {
            log.warn("Bad request DELETE /comments: invalid id");
            JsonUtil.write(resp, 400,
                    ErrorResponse.of(400, "Bad Request", "Invalid comment id", req.getRequestURI()));
        } catch (NotFoundException e) {
            log.warn("Not found DELETE /comments: {}", e.getMessage());
            JsonUtil.write(resp, 404,
                    ErrorResponse.of(404, "Not Found", e.getMessage(), req.getRequestURI()));
        } catch (ConflictException e) {
            log.warn("Conflict DELETE /comments: {}", e.getMessage());
            JsonUtil.write(resp, 409,
                    ErrorResponse.of(409, "Conflict", e.getMessage(), req.getRequestURI()));
        } catch (Exception e) {
            log.error("Unexpected error DELETE /comments", e);
            JsonUtil.write(resp, 500,
                    ErrorResponse.of(500, "Internal Server Error", "Unexpected error", req.getRequestURI()));
        }
    }
}