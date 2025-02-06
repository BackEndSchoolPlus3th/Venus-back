package com.ll.server.domain.news.news.dto;

import lombok.Getter;

import java.util.List;

@Getter

public class NewsOnlyResponse {
    List<NewsOnly> newsList;
    long count;

    public NewsOnlyResponse(List<NewsOnly> newsList){
        this.newsList=newsList;
        count=newsList.size();
    }
}
