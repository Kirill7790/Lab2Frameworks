package catalog.core.service;

import catalog.core.domain.Comment;
import catalog.core.exception.CommentTooOldException;
import catalog.core.exception.InvalidCommentDeleteException;
import catalog.core.port.BookPersistencePort;
import catalog.core.port.CommentPersistencePort;
import catalog.core.port.UserPersistencePort;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CommentServiceTest {

    private CommentPersistencePort commentPort;
    private BookPersistencePort bookPort;
    private UserPersistencePort userPort;
    private CommentService service;

    @BeforeEach
    void setUp() {
        commentPort = Mockito.mock(CommentPersistencePort.class);
        bookPort = Mockito.mock(BookPersistencePort.class);
        userPort = Mockito.mock(UserPersistencePort.class);
        service = new CommentService(commentPort, bookPort, userPort);
    }

    @Test
    void invalidBookId_throws() {
        Assertions.assertThrows(InvalidCommentDeleteException.class,
                () -> service.delete(0, 2, Instant.now()));
        Mockito.verifyNoInteractions(commentPort, bookPort, userPort);
    }

    @Test
    void invalidCommentId_throws() {
        Assertions.assertThrows(InvalidCommentDeleteException.class,
                () -> service.delete(1, 0, Instant.now()));
        Mockito.verifyNoInteractions(commentPort, bookPort, userPort);
    }

    @Test
    void nullCreatedAt_throws() {
        Assertions.assertThrows(InvalidCommentDeleteException.class,
                () -> service.delete(1, 2, null));
        Mockito.verifyNoInteractions(commentPort, bookPort, userPort);
    }

    @Test
    void commentOlderThan24h_throws() {
        Instant old = Instant.now().minus(25, ChronoUnit.HOURS);
        Assertions.assertThrows(CommentTooOldException.class,
                () -> service.delete(1, 2, old));
        Mockito.verifyNoInteractions(commentPort, bookPort, userPort);
    }

    @Test
    void validData_callsDelete() {
        long bookId = 1L;
        long commentId = 2L;
        Instant now = Instant.now();

        Mockito.when(bookPort.findById(bookId))
                .thenReturn(Optional.of(new catalog.core.domain.Book(bookId, "t", "a", 2024)));

        LocalDateTime created = LocalDateTime.ofInstant(now, ZoneOffset.UTC);
        Mockito.when(commentPort.findById(commentId))
                .thenReturn(Optional.of(new Comment(commentId, "text", created,
                        bookId, 1L, "user")));

        service.delete(bookId, commentId, now);

        Mockito.verify(commentPort).delete(bookId, commentId);
    }
}