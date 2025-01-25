package com.ll.server.domain.mock.follow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class FollowerListResponse {
    List<String> followers;
}
