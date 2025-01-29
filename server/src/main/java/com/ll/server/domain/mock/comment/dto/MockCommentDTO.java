package com.ll.server.domain.mock.comment.dto;

import com.ll.server.domain.mention.commentmention.dto.CommentMentionDTO;
import com.ll.server.domain.mock.comment.entity.MockComment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Builder
@Getter
public class MockCommentDTO {
    private Long commentId;

    private Long repostId;
    private Long repostWriterId;
    private String repostWriterName;

    private List<CommentMentionDTO> mentions;
    private String content;
    private Long commentWriterId;
    private String commentWriterName;

    public MockCommentDTO(MockComment comment){
        commentId=comment.getId();

        repostId=comment.getRepost().getId();
        repostWriterId=comment.getRepost().getUser().getId();
        repostWriterName=comment.getRepost().getUser().getNickname();

        mentions=comment.getMentions().stream()
                .map(CommentMentionDTO::new)
                .collect(Collectors.toList());

        content=comment.getContent();

        commentWriterId=comment.getUser().getId();
        commentWriterName=comment.getUser().getNickname();
    }
}
