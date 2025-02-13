package com.ll.server.domain.mention.repostmention.dto;

import com.ll.server.domain.mention.repostmention.entity.RepostMention;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RepostMentionDTO {
    private Long mentionUserId;
    private String mentionName;

    public RepostMentionDTO(RepostMention mention) {
        mentionUserId = mention.getMember().getId();
        mentionName = "@" + mention.getMember().getNickname();
    }
}
