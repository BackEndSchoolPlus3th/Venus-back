package com.ll.server.domain.follow.dto;

import com.ll.server.domain.follow.entity.Follow;
import com.ll.server.domain.mock.user.dto.MockUserDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
public class FollowerListResponse {
    private List<MockUserDTO> followers=new ArrayList<>();;
    private long count;

    public FollowerListResponse(List<Follow> follows){
        for(Follow follow:follows){
            followers.add(
                    new MockUserDTO(follow.getFollower())
            );
        }
        count=followers.size();
    }
}
