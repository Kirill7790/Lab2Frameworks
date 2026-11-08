package catalog.core.port;

import catalog.core.domain.Comment;
import java.util.List;
import java.util.Optional;

public interface CommentPersistencePort {
    List<Comment> findByBookId(Long bookId);
    List<Comment> findByUserId(Long userId);
    Optional<Comment> findById(Long id);
    Comment save(Comment comment);
    void deleteById(Long id);
}