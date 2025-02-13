package com.ll.server.domain.follow.dto;

import com.ll.server.domain.follow.entity.Follow;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class FollowDTO {
    private Long id;
    private String follower;
    private String followee;

    public FollowDTO(Follow follow) {
        if (follow != null) {
            this.id = follow.getId();
            this.follower = follow.getFollower().getNickname();
            this.followee = follow.getFollowee().getNickname();
        }
    }
}
