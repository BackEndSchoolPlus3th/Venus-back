package com.ll.server.domain.comment.repository;

import com.ll.server.domain.comment.entity.Comment;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {

    List<Comment> findCommentsByMember_Id(Long userId);

    Page<Comment> findCommentsByRepost_Id(Long repostId, Pageable pageable);

    List<Comment> findCommentsByRepost_Id(Long repostId);

    List<Comment> findCommentsByRepost_IdOrderByCreateDateAscIdAsc(Long repostId, Limit limit);

    List<Comment> findCommentsByRepost_IdAndIdGreaterThanAndCreateDateAfterOrderByCreateDateAscIdAsc(Long postId, Long lastCommentId, LocalDateTime lastTime, Limit limit);
}
