package com.ll.server.domain.mention.commentmention.dto;

import com.ll.server.domain.mention.commentmention.entity.CommentMention;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class CommentMentionDTO {
    private Long mentionUserId;
    private String mentionName;

    public CommentMentionDTO(CommentMention mention){
        mentionUserId=mention.getUser().getId();
        mentionName="@"+mention.getUser().getNickname();
    }
}
