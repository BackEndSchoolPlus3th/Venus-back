package com.ll.server.domain.news.news.dto;

import com.ll.server.domain.news.news.entity.News;
import com.ll.server.domain.repost.dto.RepostUnderNews;
import com.ll.server.global.response.response.CustomPage;

public class NewsPageDetail {
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
    private CustomPage<RepostUnderNews> reposts;

    public NewsPageDetail(News news, CustomPage<RepostUnderNews> reposts){
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
