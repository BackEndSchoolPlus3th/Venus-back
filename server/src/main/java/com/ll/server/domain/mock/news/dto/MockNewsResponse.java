package com.ll.server.domain.mock.news.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class MockNewsResponse {
    List<MockNewsDTO> newsList;
    long count;

    public MockNewsResponse(List<MockNewsDTO> newsList){
        this.newsList=newsList;
        count=newsList.size();
    }

}
