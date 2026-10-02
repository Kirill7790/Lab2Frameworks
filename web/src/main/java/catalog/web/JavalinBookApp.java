package catalog.web;


import catalog.core.service.BookService;
import catalog.core.service.CommentService;
import catalog.persistence.DatabaseInitializer;
import catalog.persistence.JdbcCatalogRepository;
import catalog.persistence.JdbcCommentRepository;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.h2.jdbcx.JdbcDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

public class JavalinBookApp {
    private static final Logger log = LoggerFactory.getLogger(JavalinBookApp.class);

    public static void main(String[] args) {
        // --- Ініціалізація БД та сервісів (composition root) ---
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:catalog;DB_CLOSE_DELAY=-1;MODE=PostgreSQL");
        ds.setUser("sa");
        ds.setPassword("");
        DatabaseInitializer.init(ds);

        JdbcCatalogRepository catalog = new JdbcCatalogRepository(ds);
        JdbcCommentRepository comments = new JdbcCommentRepository(ds);

        BookService bookService = new BookService(catalog);
        CommentService commentService = new CommentService(comments, catalog);

        // --- Створення Javalin ---
        Javalin app = Javalin.create(config -> {
            config.showJavalinBanner = false;
            config.jsonMapper(new io.javalin.json.JavalinJackson()); // Jackson для JSON
        });

        // --- Middleware: логування кожного запиту ---
        app.before(ctx -> {
            log.info("--> {} {}", ctx.method(), ctx.path());
        });

        app.after(ctx -> {
            log.info("<-- {} {} -> {}", ctx.method(), ctx.path(), ctx.status());
        });

        // --- Централізована обробка помилок ---
        app.exception(catalog.core.exception.NotFoundException.class, (e, ctx) -> {
            log.warn("404 Not Found: {} -> {}", ctx.path(), e.getMessage());
            ctx.status(404).json(ErrorResponse.of(404, "Not Found", e.getMessage(), ctx.path()));
        });

        app.exception(catalog.core.exception.ValidationException.class, (e, ctx) -> {
            log.warn("400 Bad Request: {} -> {}", ctx.path(), e.getMessage());
            ctx.status(400).json(ErrorResponse.of(400, "Bad Request", e.getMessage(), ctx.path()));
        });

        app.exception(catalog.core.exception.ConflictException.class, (e, ctx) -> {
            log.warn("409 Conflict: {} -> {}", ctx.path(), e.getMessage());
            ctx.status(409).json(ErrorResponse.of(409, "Conflict", e.getMessage(), ctx.path()));
        });

        app.exception(NumberFormatException.class, (e, ctx) -> {
            log.warn("400 Bad Request: {} -> invalid number", ctx.path());
            ctx.status(400).json(ErrorResponse.of(400, "Bad Request", "Invalid number", ctx.path()));
        });

        app.exception(Exception.class, (e, ctx) -> {
            log.error("500 Internal Server Error on {} {}", ctx.method(), ctx.path(), e);
            ctx.status(500).json(ErrorResponse.of(500, "Internal Server Error",
                    "Unexpected error", ctx.path()));
        });

        // --- Маршрути ---
        registerRoutes(app, bookService, commentService);

        // --- Старт ---
        app.start(8080);
        log.info("Javalin started on http://localhost:8080");
    }

    private static void registerRoutes(Javalin app, BookService books, CommentService comments) {

        // GET /books?q=...&page=0&size=20&sort=title,asc
        app.get("/books", ctx -> {
            String q = ctx.queryParam("q");
            int page = parseIntOrDefault(ctx.queryParam("page"), 0);
            int size = parseIntOrDefault(ctx.queryParam("size"), 20);
            String sort = ctx.queryParam("sort");

            var pageRequest = new catalog.core.domain.PageRequest(page, size, sort);
            var result = books.search(q, pageRequest);
            ctx.json(result);
        });

        // GET /books/{id}
        app.get("/books/{id}", ctx -> {
            long id = ctx.pathParamAsClass("id", Long.class).get();
            var book = books.getById(id);
            var commentsList = comments.listByBook(id);
            ctx.json(java.util.Map.of("book", book, "comments", commentsList));
        });

        // POST /books/{id}/comments
        app.post("/books/{id}/comments", ctx -> {
            long bookId = ctx.pathParamAsClass("id", Long.class).get();
            var body = ctx.bodyAsClass(java.util.Map.class);
            String author = (String) body.get("author");
            String text = (String) body.get("text");

            var created = comments.addComment(bookId, author, text);
            ctx.status(201).json(created);
        });

        // DELETE /comments/{id}
        app.delete("/comments/{id}", ctx -> {
            long commentId = ctx.pathParamAsClass("id", Long.class).get();
            comments.deleteComment(commentId);
            ctx.status(204);
        });
    }

    private static int parseIntOrDefault(String value, int defaultValue) {
        if (value == null || value.isBlank()) return defaultValue;
        try { return Integer.parseInt(value); }
        catch (NumberFormatException e) { throw new NumberFormatException(value); }
    }
}
