package com.ll.server.domain.mock.follow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
public class MockFollowRequest {
    private Long followerId;
    private Long followeeId;
}
