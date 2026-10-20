package catalog.core.service;

import catalog.core.domain.Comment;
import catalog.core.exception.ConflictException;
import catalog.core.exception.NotFoundException;
import catalog.core.exception.ValidationException;
import catalog.core.port.CatalogRepositoryPort;
import catalog.core.port.CommentRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
public class CommentService {
    private static final Logger log = LoggerFactory.getLogger(CommentService.class);
    private static final Duration EDIT_WINDOW = Duration.ofHours(24);

    private final CommentRepositoryPort commentRepository;
    private final CatalogRepositoryPort catalogRepository;

    public CommentService(CommentRepositoryPort commentRepository,
                          CatalogRepositoryPort catalogRepository) {
        this.commentRepository = commentRepository;
        this.catalogRepository = catalogRepository;
    }

    public List<Comment> listAll() {
        return commentRepository.findAll();
    }

    public List<Comment> listByBook(long bookId) {
        if (bookId <= 0) {
            throw new ValidationException("bookId must be positive");
        }
        catalogRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundException("Book " + bookId + " not found"));
        return commentRepository.findByBookId(bookId);
    }

    public Comment getById(long id) {
        if (id <= 0) {
            throw new ValidationException("id must be positive");
        }
        return commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Comment " + id + " not found"));
    }

    public Comment addComment(long bookId, String author, String text) {
        if (bookId <= 0) {
            throw new ValidationException("bookId must be positive");
        }
        catalogRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundException("Book " + bookId + " not found"));
        if (author == null || author.isBlank()) {
            throw new ValidationException("author must not be blank");
        }
        if (text == null || text.isBlank() || text.length() > 2000) {
            throw new ValidationException("text must be 1..2000 chars");
        }
        Comment saved = commentRepository.save(
                new Comment(null, bookId, author.trim(), text.trim(), Instant.now()));
        log.info("Comment created id={} bookId={} author={}", saved.getId(), bookId, author);
        return saved;
    }

    public void deleteComment(long commentId) {
        if (commentId <= 0) {
            throw new ValidationException("commentId must be positive");
        }
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment " + commentId + " not found"));

        Duration age = Duration.between(comment.getCreatedAt(), Instant.now());
        if (age.compareTo(EDIT_WINDOW) > 0) {
            throw new ConflictException("Comment can be deleted only within 24 hours");
        }
        commentRepository.deleteById(commentId);
        log.info("Comment deleted id={} bookId={} ageMinutes={}",
                commentId, comment.getBookId(), age.toMinutes());
    }
}