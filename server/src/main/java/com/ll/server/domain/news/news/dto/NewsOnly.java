package com.ll.server.domain.news.news.dto;

import com.ll.server.domain.elasticsearch.news.doc.NewsDoc;
import com.ll.server.domain.news.news.entity.News;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    private LocalDateTime publishedAt;

    public NewsOnly(News news) {
        id = news.getId();
        title = news.getTitle();
        category = news.getCategory().getCategory();
        author = news.getAuthor();
        publishedAt = news.getPublishedAt();
        publisherName = news.getPublisher();
        imageUrl = news.getImageUrl();
        thumbnailUrl = news.getThumbnailUrl();
        contentUrl = news.getContentUrl();
        content = news.getContent();
    }

    public NewsOnly(NewsDoc newsDoc) {
        id = newsDoc.getId();
        title = newsDoc.getTitle();
        category = newsDoc.getCategory().getCategory();
        author = newsDoc.getAuthor();
        publishedAt = newsDoc.getPublishedAt();
        publisherName = newsDoc.getPublisher();
        imageUrl = newsDoc.getImageUrl();
        thumbnailUrl = newsDoc.getThumbnailUrl();
        contentUrl = newsDoc.getContentUrl();
        content = newsDoc.getContent();
    }

    public NewsOnly(NewsDTO newsDTO) {
        id = newsDTO.getId();
        title = newsDTO.getTitle();
        category = newsDTO.getCategory();
        author = newsDTO.getAuthor();
        publishedAt = newsDTO.getPublishedAt();
        publisherName = newsDTO.getPublisherName();
        imageUrl = newsDTO.getImageUrl();
        thumbnailUrl = newsDTO.getThumbnailUrl();
        contentUrl = newsDTO.getContentUrl();
        content = newsDTO.getContent();
    }
}
