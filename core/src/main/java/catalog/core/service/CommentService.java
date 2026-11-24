package catalog.core.service;

import catalog.core.domain.Comment;
import catalog.core.exception.NotFoundException;
import catalog.core.exception.ValidationException;
import catalog.core.port.BookPersistencePort;
import catalog.core.port.CommentPersistencePort;
import catalog.core.port.UserPersistencePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;

import catalog.core.exception.BookNotFoundException;
import catalog.core.exception.CommentTooOldException;
import catalog.core.exception.InvalidCommentDeleteException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

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

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public List<Comment> getByBook(Long bookId) {
        bookPort.findById(bookId)
                .orElseThrow(() -> new NotFoundException("Book " + bookId + " not found"));
        return commentPort.findByBookId(bookId);
    }

    @PreAuthorize("isAuthenticated()")
    public List<Comment> getByUser(Long userId) {
        userPort.findById(userId)
                .orElseThrow(() -> new NotFoundException("User " + userId + " not found"));
        return commentPort.findByUserId(userId);
    }

    @Transactional
    @PreAuthorize("isAuthenticated()")
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
    public void delete(long bookId, long commentId, Instant createdAt) {
        if (bookId <= 0) {
            throw new InvalidCommentDeleteException("bookId must be > 0");
        }
        if (commentId <= 0) {
            throw new InvalidCommentDeleteException("commentId must be > 0");
        }
        if (createdAt == null) {
            throw new InvalidCommentDeleteException("createdAt is required");
        }

        LocalDateTime created = LocalDateTime.ofInstant(createdAt, ZoneOffset.UTC);
        if (created.isBefore(LocalDateTime.now(ZoneOffset.UTC).minus(24, ChronoUnit.HOURS))) {
            throw new CommentTooOldException(
                    "Comment created more than 24 hours ago cannot be deleted");
        }

        bookPort.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book " + bookId + " not found"));

        if (!commentPort.existsByIdAndBookId(commentId, bookId)) {
            throw new InvalidCommentDeleteException(
                    "Comment " + commentId + " does not belong to book " + bookId);
        }
        commentPort.delete(bookId, commentId);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public void deleteComment(Long id) {
        commentPort.deleteById(id);
    }
}