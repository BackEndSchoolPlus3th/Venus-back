package com.ll.server.domain.news.news.dto;


import com.ll.server.domain.news.news.entity.News;
import com.ll.server.domain.repost.dto.RepostUnderNews;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewsDTO {
    private Long id;
    private String title;
    private String content;
    private String author;
    private String publisherName; // From Publisher entity
    private String imageUrl;
    private String thumbnailUrl;
    private String contentUrl;
    private String category;
    private String publishedAt;
    private List<RepostUnderNews> reposts;

    public NewsDTO(News news) {
        id = news.getId();
        title = news.getTitle();
        content = news.getContent();
        author = news.getAuthor();
        publisherName = news.getPublisher();
        imageUrl = news.getImageUrl();
        thumbnailUrl = news.getThumbnailUrl();
        contentUrl = news.getContentUrl();
        category = news.getCategory().getCategory();
        publishedAt = news.getPublishedAt();
        reposts=news.getReposts().stream().map(RepostUnderNews::new).collect(Collectors.toList());
    }

}