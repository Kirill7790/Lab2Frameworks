package catalog.persistence.repository;

import catalog.persistence.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    @Query("SELECT c FROM CommentEntity c JOIN FETCH c.user JOIN FETCH c.book WHERE c.book.id = :bookId ORDER BY c.createdAt DESC")
    List<CommentEntity> findByBookIdOrderByCreatedAtDesc(@Param("bookId") Long bookId);

    @Query("SELECT c FROM CommentEntity c JOIN FETCH c.user JOIN FETCH c.book WHERE c.user.id = :userId")
    List<CommentEntity> findByUserId(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM CommentEntity c WHERE c.id = :commentId AND c.book.id = :bookId")
    int deleteByBookIdAndCommentId(@Param("bookId") Long bookId,
                                   @Param("commentId") Long commentId);

    boolean existsByIdAndBookId(Long id, Long bookId);
}