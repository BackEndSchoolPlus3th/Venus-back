package com.ll.server.domain.mock.follow.dto;

import com.ll.server.domain.mock.follow.entity.MockFollow;
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

    public FolloweeListResponse(List<MockFollow> follows){
        for(MockFollow follow:follows){
            followees.add(
                    new MockUserDTO(follow.getFollowee())
            );
        }
        count=followees.size();
    }
}
