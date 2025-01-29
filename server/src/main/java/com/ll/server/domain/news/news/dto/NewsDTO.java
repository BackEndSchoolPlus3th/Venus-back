package com.ll.server.domain.news.news.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

}