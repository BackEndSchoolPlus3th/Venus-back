package com.ll.server.domain.like.dto;

import com.ll.server.domain.like.entity.Like;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class LikeDTO {
    private Long id;
    private Long repostId;
    private Long repostWriterId;
    private Long checkedUserId;
    private String checkedUserName;

    public LikeDTO(Like like){
        id=like.getId();
        repostId=like.getRepost().getId();
        repostWriterId=like.getRepost().getUser().getId();
        checkedUserId=like.getUser().getId();
        checkedUserName=like.getUser().getNickname();
    }
}