package com.ll.server.domain.news.news.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class NewsApiResponseParam<T> {
    private Detail detail;

    @JsonProperty("total_items")
    private int totalItems;
    @JsonProperty("total_pages")
    private int totalPages;
    private int page;
    @JsonProperty("page_size")
    private int pageSize;
    private List<NewsArticleParam> data;

    @Getter
    public static class Detail {
        private String message;
        private String code;
        private boolean ok;
    }
}
