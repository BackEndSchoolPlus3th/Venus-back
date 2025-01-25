package com.ll.server.domain.mock.follow.dto;

import com.ll.server.domain.mock.follow.entity.MockFollow;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class MockFollowDTO {
    private Long id;
    private String follower;
    private String followee;

    public MockFollowDTO(MockFollow follow){
        if(follow != null) {
            this.id = follow.getId();
            this.follower = follow.getFollower().getNickname();
            this.followee = follow.getFollowee().getNickname();
        }
    }
}
