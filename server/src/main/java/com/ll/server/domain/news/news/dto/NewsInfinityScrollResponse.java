package com.ll.server.domain.news.news.dto;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class NewsInfinityScrollResponse {
    List<NewsOnly> newsList;
    LocalDateTime lastTime;
    long lastId;

    public NewsInfinityScrollResponse(List<NewsOnly> newsList) {
        if (newsList == null || newsList.isEmpty()) {
            this.newsList = null;
            lastTime = null;
            lastId = -1;
        } else {
            this.newsList = newsList;
            lastTime = newsList.getLast().getPublishedAt();
            lastId = newsList.getLast().getId();
        }
    }

}
