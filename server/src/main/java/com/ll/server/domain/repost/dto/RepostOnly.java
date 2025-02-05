package com.ll.server.domain.repost.dto;

import com.ll.server.domain.mention.repostmention.dto.RepostMentionDTO;
import com.ll.server.domain.repost.entity.Repost;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Builder
@Getter
public class RepostOnly {
    private Long repostId;
    private Long writerId;
    private String nickname;
    private String content;
    private List<RepostMentionDTO> mentions;
    private int commentCount;
    private int likeCount;
    private String imageUrl;

    public RepostOnly(Repost repost){
        repostId=repost.getId();
        writerId=repost.getMember().getId();
        nickname=repost.getMember().getNickname();
        content=repost.getContent();
        mentions=repost.getMentions().stream()
                .map(RepostMentionDTO::new)
                .collect(Collectors.toList());

        commentCount= repost.getComments().size();

        likeCount= repost.getLikes().size();

        imageUrl = repost.getImageUrl();

    }
}
