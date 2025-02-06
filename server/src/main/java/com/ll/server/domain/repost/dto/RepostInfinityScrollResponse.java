package com.ll.server.domain.repost.dto;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class RepostInfinityScrollResponse {
    private List<RepostOnly> reposts;
    private long count;
    private long lastId;
    private LocalDateTime lastTime;

    public RepostInfinityScrollResponse(List<RepostOnly> reposts){
        if(reposts==null || reposts.isEmpty()){
            this.reposts=null;
            count=0;
            lastId=-1;
            lastTime=null;
        }else{
            this.reposts=reposts;
            count=reposts.size();
            lastId=reposts.getLast().getRepostId();
            lastTime=reposts.getLast().getCreateDate();
        }

    }
}
