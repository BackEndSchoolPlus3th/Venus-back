package com.ll.server.domain.comment.repository;

import com.ll.server.domain.comment.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {

    List<Comment> findCommentsByMember_Id(Long userId);

    Page<Comment> findCommentsByRepostId(Long repostId, Pageable pageable);

    List<Comment> findTop10ById(Long commentId);

    List<Comment> findTop10ByIdGreaterThan(Long commentId);
}
