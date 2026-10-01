package catalog.web;


import catalog.core.service.BookService;
import catalog.core.service.CommentService;
import catalog.persistence.DatabaseInitializer;
import catalog.persistence.JdbcCatalogRepository;
import catalog.persistence.JdbcCommentRepository;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.h2.jdbcx.JdbcDataSource;

import javax.sql.DataSource;

@WebListener
public class AppContextListener implements ServletContextListener {
    public static final String BOOK_SERVICE = "bookService";
    public static final String COMMENT_SERVICE = "commentService";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:catalog;DB_CLOSE_DELAY=-1;MODE=PostgreSQL");
        ds.setUser("sa");
        ds.setPassword("");

        DatabaseInitializer.init(ds);

        var catalog = new JdbcCatalogRepository(ds);
        var comments = new JdbcCommentRepository(ds);

        sce.getServletContext().setAttribute(BOOK_SERVICE, new BookService(catalog));
        sce.getServletContext().setAttribute(COMMENT_SERVICE, new CommentService(comments, catalog));
    }
}
