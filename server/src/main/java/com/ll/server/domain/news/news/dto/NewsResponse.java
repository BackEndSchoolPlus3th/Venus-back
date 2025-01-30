package com.ll.server.domain.news.news.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class NewsResponse {
    List<NewsDTO> newsList;
    long count;

    public NewsResponse(List<NewsDTO> newsList){
        this.newsList=newsList;
        count=newsList.size();
    }

}
