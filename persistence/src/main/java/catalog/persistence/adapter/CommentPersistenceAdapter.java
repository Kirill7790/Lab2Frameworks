package catalog.persistence.adapter;

import catalog.core.domain.Comment;
import catalog.core.port.CommentPersistencePort;
import catalog.persistence.entity.CommentEntity;
import catalog.persistence.repository.CommentRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CommentPersistenceAdapter implements CommentPersistencePort {

    private final CommentRepository repo;

    public CommentPersistenceAdapter(CommentRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<Comment> findByBookId(Long bookId) {
        return repo.findByBookIdOrderByCreatedAtDesc(bookId).stream()
                .map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Comment> findByUserId(Long userId) {
        return repo.findByUserId(userId).stream()
                .map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<Comment> findById(Long id) {
        return repo.findById(id).map(this::toDomain);
    }

    @Override
    public Comment save(Comment c) {
        var book = new catalog.persistence.entity.BookEntity();
        book.setId(c.getBookId());
        var user = new catalog.persistence.entity.UserEntity();
        user.setId(c.getUserId());

        CommentEntity e = new CommentEntity(c.getText(), book, user);
        CommentEntity saved = repo.save(e);
        return toDomain(saved);
    }

    @Override
    public void deleteById(Long id) {
        repo.deleteById(id);
    }

    private Comment toDomain(CommentEntity e) {
        return new Comment(
                e.getId(),
                e.getText(),
                e.getCreatedAt(),
                e.getBook() != null ? e.getBook().getId() : null,
                e.getUser() != null ? e.getUser().getId() : null,
                e.getUser() != null ? e.getUser().getUsername() : null
        );
    }
}