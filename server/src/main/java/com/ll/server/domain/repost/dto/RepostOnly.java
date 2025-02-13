package com.ll.server.domain.repost.dto;

import com.ll.server.domain.repost.entity.Repost;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Getter
public class RepostOnly {
    private Long repostId;
    private Long writerId;
    private String nickname;
    private String content;
    private int commentCount;
    private int likeCount;
    private String imageUrl;
    private LocalDateTime createDate;
    private String memberProfileImageUrl;

    public RepostOnly(Repost repost) {
        repostId = repost.getId();
        writerId = repost.getMember().getId();
        nickname = repost.getMember().getNickname();
        content = repost.getContent();

        commentCount = repost.getComments().size();

        likeCount = repost.getLikes().size();

        imageUrl = repost.getImageUrl();
        createDate = repost.getCreateDate();

        memberProfileImageUrl = repost.getMember().getProfileUrl();

    }
}
