package com.ll.server.domain.mock.like.dto;

import lombok.Getter;

import java.util.List;


@Getter
public class MockLikeResponse {
    private List<MockLikeDTO> likes;
    private long count;

    public MockLikeResponse(List<MockLikeDTO> likes){
        this.likes=likes;
        count=likes.size();
    }
}
