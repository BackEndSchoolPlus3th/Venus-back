package com.ll.server.domain.mention.commentmention.repository;

import com.ll.server.domain.mention.commentmention.entity.CommentMention;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentMentionRepository extends JpaRepository<CommentMention, Long> {
    List<CommentMention> findCommentMentionsByComment_Id(Long commentId);
}
