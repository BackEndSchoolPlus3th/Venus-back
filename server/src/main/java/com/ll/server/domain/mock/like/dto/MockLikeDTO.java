package com.ll.server.domain.mock.like.dto;

import com.ll.server.domain.mock.like.entity.MockLike;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class MockLikeDTO {
    private Long id;
    private Long repostId;
    private Long repostWriterId;
    private Long checkedUserId;
    private String checkedUserName;

    public MockLikeDTO(MockLike like){
        id=like.getId();
        repostId=like.getRepost().getId();
        repostWriterId=like.getUser().getId();
        checkedUserId=like.getUser().getId();
        checkedUserName=like.getUser().getNickname();
    }
}
