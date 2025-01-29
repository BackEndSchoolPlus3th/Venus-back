package com.ll.server.domain.mention.commentmention.service;


import com.ll.server.domain.mention.commentmention.entity.CommentMention;
import com.ll.server.domain.mock.comment.entity.MockComment;
import com.ll.server.domain.mention.commentmention.repository.CommentMentionRepository;
import com.ll.server.domain.mock.user.entity.MockUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentMentionService {
    CommentMentionRepository commentMentionRepository;

    @Transactional
    public CommentMention save(MockComment comment, MockUser user){
        CommentMention mention=
                CommentMention.builder()
                        .comment(comment)
                        .user(user)
                        .build();

        return commentMentionRepository.save(mention);
    }

    public List<CommentMention> findByComment(Long commentId){
        return commentMentionRepository.findCommentMentionsByComment_Id(commentId);
    }
}
