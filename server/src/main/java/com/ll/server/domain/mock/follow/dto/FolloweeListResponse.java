package com.ll.server.domain.mock.follow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class FolloweeListResponse {
    List<String> followees;
}
