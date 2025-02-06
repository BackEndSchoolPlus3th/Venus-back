package com.ll.server.domain.news.news.dto;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class NewsInfinityScrollResponse {
    List<NewsOnly> newsList;
    long lastId;
    LocalDateTime lastTime;

    public NewsInfinityScrollResponse(List<NewsOnly> newsList){
        if(newsList==null || newsList.isEmpty()){
            this.newsList=null;
            lastId=-1;
            lastTime=null;
        }else{
            this.newsList=newsList;
            lastId=newsList.getLast().getId();
            lastTime=newsList.getLast().getPublishedAt();
        }
    }

}
