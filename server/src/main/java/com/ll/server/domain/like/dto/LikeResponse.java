package com.ll.server.domain.like.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class LikeResponse {
    private List<LikeDTO> likes;
    private long count;

    public LikeResponse(List<LikeDTO> likes){
        this.likes=likes;
        count=likes.size();
    }
}
