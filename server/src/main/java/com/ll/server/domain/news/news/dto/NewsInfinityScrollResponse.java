package com.ll.server.domain.news.news.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class NewsInfinityScrollResponse {
    List<NewsOnly> newsList;
    long lastId;

    public NewsInfinityScrollResponse(List<NewsOnly> newsList){
        this.newsList=newsList;
        this.lastId=newsList.getLast().getId();
    }

}
