package com.ll.server.domain.news.news.dto;

import com.ll.server.domain.news.news.entity.News;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
//검색 결과를 띄울 때 사용
public class NewsOnly {
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

    public NewsOnly(News news){
        id=news.getId();
        title=news.getTitle();
        category=news.getCategory().getCategory();
        author=news.getAuthor();
        publishedAt=news.getPublishedAt();
        publisherName=news.getPublisher();
        imageUrl=news.getImageUrl();
        thumbnailUrl=news.getThumbnailUrl();
        contentUrl=news.getContentUrl();
        content=news.getContent();
    }
}
