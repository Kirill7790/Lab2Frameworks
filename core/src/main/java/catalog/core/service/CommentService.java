package catalog.core.service;

import catalog.core.domain.Comment;
import catalog.core.exception.NotFoundException;
import catalog.core.exception.ValidationException;
import catalog.core.port.BookPersistencePort;
import catalog.core.port.CommentPersistencePort;
import catalog.core.port.UserPersistencePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService {
    private final CommentPersistencePort commentPort;
    private final BookPersistencePort bookPort;
    private final UserPersistencePort userPort;

    public CommentService(CommentPersistencePort commentPort,
                          BookPersistencePort bookPort,
                          UserPersistencePort userPort) {
        this.commentPort = commentPort;
        this.bookPort = bookPort;
        this.userPort = userPort;
    }

    public List<Comment> getByBook(Long bookId) {
        bookPort.findById(bookId)
                .orElseThrow(() -> new NotFoundException("Book " + bookId + " not found"));
        return commentPort.findByBookId(bookId);
    }

    public List<Comment> getByUser(Long userId) {
        userPort.findById(userId)
                .orElseThrow(() -> new NotFoundException("User " + userId + " not found"));
        return commentPort.findByUserId(userId);
    }

    @Transactional
    public Comment addComment(Long bookId, Long userId, String text) {
        if (text == null || text.isBlank() || text.length() > 2000) {
            throw new ValidationException("Comment text must be 1..2000 chars");
        }
        bookPort.findById(bookId)
                .orElseThrow(() -> new NotFoundException("Book " + bookId + " not found"));
        userPort.findById(userId)
                .orElseThrow(() -> new NotFoundException("User " + userId + " not found"));

        return commentPort.save(new Comment(null, text.trim(), LocalDateTime.now(),
                bookId, userId, null));
    }

    @Transactional
    public void deleteComment(Long id) {
        commentPort.deleteById(id);
    }
}