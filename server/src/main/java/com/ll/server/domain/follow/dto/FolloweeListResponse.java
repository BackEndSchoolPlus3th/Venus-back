package com.ll.server.domain.follow.dto;

import com.ll.server.domain.follow.entity.Follow;
import com.ll.server.domain.member.dto.MemberDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class FolloweeListResponse {
    private List<MemberDto> followees=new ArrayList<>();
    private long count;

    public FolloweeListResponse(List<Follow> follows){
        for(Follow follow:follows){
            followees.add(
                    new MemberDto(follow.getFollowee())
            );
        }
        count=followees.size();
    }
}
