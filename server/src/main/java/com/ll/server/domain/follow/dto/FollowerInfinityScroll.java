package com.ll.server.domain.follow.dto;

import com.ll.server.domain.member.dto.MemberDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class FollowerInfinityScroll {
    private List<MemberDto> followers;
    private long lastId;
}
