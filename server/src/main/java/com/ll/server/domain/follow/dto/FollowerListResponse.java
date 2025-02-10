package com.ll.server.domain.follow.dto;

import com.ll.server.domain.member.dto.MemberDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class FollowerListResponse {
    private List<MemberDto> followers;
    private long totalCount;
    private long lastId;
}
