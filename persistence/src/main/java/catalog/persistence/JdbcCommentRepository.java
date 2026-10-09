package catalog.persistence;


import catalog.core.domain.Comment;
import catalog.core.port.CommentRepositoryPort;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcCommentRepository implements CommentRepositoryPort {
    private final DataSource dataSource;

    public JdbcCommentRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Comment> findByBookId(long bookId) {
        String sql = "SELECT id, book_id, author, text, created_at FROM comments WHERE book_id = ? ORDER BY created_at DESC";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Comment> list = new ArrayList<>();
                while (rs.next()) list.add(map(rs));
                return list;
            }
        } catch (SQLException e) {
            throw new RuntimeException("findByBookId failed", e);
        }
    }

    @Override
    public Optional<Comment> findById(long id) {
        String sql = "SELECT id, book_id, author, text, created_at FROM comments WHERE id = ?";
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

    @Override
    public Comment save(Comment comment) {
        String sql = "INSERT INTO comments (book_id, author, text, created_at) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, comment.getBookId());
            ps.setString(2, comment.getAuthor());
            ps.setString(3, comment.getText());
            ps.setTimestamp(4, Timestamp.from(comment.getCreatedAt()));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return new Comment(keys.getLong(1), comment.getBookId(),
                        comment.getAuthor(), comment.getText(), comment.getCreatedAt());
            }
        } catch (SQLException e) {
            throw new RuntimeException("save failed", e);
        }
    }

    @Override
    public boolean deleteById(long id) {
        String sql = "DELETE FROM comments WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("deleteById failed", e);
        }
    }

    private Comment map(ResultSet rs) throws SQLException {
        return new Comment(
                rs.getLong("id"),
                rs.getLong("book_id"),
                rs.getString("author"),
                rs.getString("text"),
                rs.getTimestamp("created_at").toInstant());
    }
}
