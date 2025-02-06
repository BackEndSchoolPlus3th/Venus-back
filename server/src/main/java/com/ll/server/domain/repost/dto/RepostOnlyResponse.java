package com.ll.server.domain.repost.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class RepostOnlyResponse {
    private List<RepostOnly> reposts;
    private long count;

    public RepostOnlyResponse(List<RepostOnly> reposts){
        this.reposts=reposts;
        count=reposts.size();
    }
}
