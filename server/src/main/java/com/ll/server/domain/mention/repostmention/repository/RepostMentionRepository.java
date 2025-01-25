package com.ll.server.domain.mention.repostmention.repository;

import com.ll.server.domain.mention.repostmention.entity.RepostMention;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RepostMentionRepository extends JpaRepository<RepostMention, Long> {
    List<RepostMention> findRepostMentionsByRepost_Id(Long repostId);
}
