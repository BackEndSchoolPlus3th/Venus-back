package com.ll.server.domain.news.news.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ll.server.domain.news.news.entity.News;
import com.ll.server.domain.news.news.enums.NewsCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NewsArticleParam {
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

    public News toEntity(NewsCategory category) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        return News.builder()
                .title(title)
                .content(summary)
                .publisher(publisher)
                .author(author)
                .imageUrl(imageUrl)
                .thumbnailUrl(thumbnailUrl)
                .contentUrl(contentUrl)
                .publishedAt(LocalDateTime.parse(publishedAt, formatter))
                .category(category)
                .build();
    }
}
