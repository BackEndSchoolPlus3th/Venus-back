package com.ll.server.domain.repost.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class RepostListResponse {
    private List<RepostDTO> reposts;
    private long count;

    public RepostListResponse(List<RepostDTO> reposts){
        this.reposts=reposts;
        count=reposts.size();
    }
}
