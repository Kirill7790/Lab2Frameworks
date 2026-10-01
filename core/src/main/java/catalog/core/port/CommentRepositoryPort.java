package catalog.core.port;


import catalog.core.domain.Comment;
import java.util.List;
import java.util.Optional;

public interface CommentRepositoryPort {
    List<Comment> findByBookId(long bookId);
    Optional<Comment> findById(long id);
    Comment save(Comment comment);
    boolean deleteById(long id);
}
