package com.ll.server.domain.news.news.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GlobalNewsFetchParam {
    @JsonProperty("title_ko")
    private String titleKo;
    private String publisher;
    private String author;
    @JsonProperty("summary_ko")
    private String summaryKo;
    @JsonProperty("image_url")
    private String imageUrl;
    @JsonProperty("thumbnail_url")
    private String thumbnailUrl;
    @JsonProperty("content_url")
    private String contentUrl;
    @JsonProperty("published_at")
    private String publishedAt;
}
