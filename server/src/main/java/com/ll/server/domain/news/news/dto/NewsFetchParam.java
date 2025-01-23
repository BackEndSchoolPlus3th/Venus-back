package com.ll.server.domain.news.news.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NewsFetchParam {
    private String title;
    private String publisher;
    private String author;
    private String summary;
    @JsonProperty("image_url")
    private String imageUrl;
    @JsonProperty("thumbnail_url")
    private String thumbnailUrl;
    @JsonProperty("content_url")
    private String contentUrl;
    @JsonProperty("published_at")
    private String publishedAt;
}
