package com.ll.server.domain.follow.dto;

import com.ll.server.domain.follow.entity.Follow;
import com.ll.server.domain.mock.user.dto.MockUserDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class FolloweeListResponse {
    private List<MockUserDTO> followees=new ArrayList<>();
    private long count;

    public FolloweeListResponse(List<Follow> follows){
        for(Follow follow:follows){
            followees.add(
                    new MockUserDTO(follow.getFollowee())
            );
        }
        count=followees.size();
    }
}
