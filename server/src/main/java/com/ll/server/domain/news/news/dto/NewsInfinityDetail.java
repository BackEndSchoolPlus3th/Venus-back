package com.ll.server.domain.news.news.dto;

import com.ll.server.domain.news.news.entity.News;
import com.ll.server.domain.repost.dto.RepostInfinityResponse;

public class NewsInfinityDetail {
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
    private RepostInfinityResponse reposts;

    public NewsInfinityDetail(News news, RepostInfinityResponse reposts){
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
        this.reposts=reposts;
    }
}
