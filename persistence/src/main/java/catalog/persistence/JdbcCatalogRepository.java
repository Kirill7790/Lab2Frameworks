package catalog.persistence;


import catalog.core.domain.Book;
import catalog.core.domain.Page;
import catalog.core.domain.PageRequest;
import catalog.core.port.CatalogRepositoryPort;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcCatalogRepository implements CatalogRepositoryPort {
    private final DataSource dataSource;

    public JdbcCatalogRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Page<Book> findBooks(String query, PageRequest request) {
        String like = "%" + query.toLowerCase() + "%";
        String sortCol = resolveSortColumn(request.getSort());
        String direction = resolveDirection(request.getSort());

        String countSql = "SELECT COUNT(*) FROM books WHERE LOWER(title) LIKE ? OR LOWER(author) LIKE ?";
        String dataSql = "SELECT id, title, author, isbn, published_at FROM books " +
                "WHERE LOWER(title) LIKE ? OR LOWER(author) LIKE ? " +
                "ORDER BY " + sortCol + " " + direction + " LIMIT ? OFFSET ?";

        try (Connection conn = dataSource.getConnection()) {
            long total;
            try (PreparedStatement ps = conn.prepareStatement(countSql)) {
                ps.setString(1, like);
                ps.setString(2, like);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    total = rs.getLong(1);
                }
            }
            List<Book> books = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(dataSql)) {
                ps.setString(1, like);
                ps.setString(2, like);
                ps.setInt(3, request.getSize());
                ps.setInt(4, request.getOffset());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) books.add(map(rs));
                }
            }
            return new Page<>(books, request.getPage(), request.getSize(), total);
        } catch (SQLException e) {
            throw new RuntimeException("findBooks failed", e);
        }
    }

    @Override
    public Optional<Book> findById(long id) {
        String sql = "SELECT id, title, author, isbn, published_at FROM books WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("findById failed", e);
        }
    }

    private Book map(ResultSet rs) throws SQLException {
        Timestamp ts = rs.getTimestamp("published_at");
        return new Book(
                rs.getLong("id"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getString("isbn"),
                ts == null ? null : ts.toInstant());
    }

    private String resolveSortColumn(String sort) {
        String col = sort.split(",")[0].trim();
        return switch (col) {
            case "author" -> "author";
            case "id" -> "id";
            default -> "title";
        };
    }

    private String resolveDirection(String sort) {
        String[] parts = sort.split(",");
        return (parts.length > 1 && "desc".equalsIgnoreCase(parts[1].trim())) ? "DESC" : "ASC";
    }
}
