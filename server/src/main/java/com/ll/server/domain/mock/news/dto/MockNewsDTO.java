package com.ll.server.domain.mock.news.dto;


import com.ll.server.domain.mock.news.entity.MockNews;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MockNewsDTO {
    private Long id;
    private String title;
    private String content;
    private String author;
    private String publisher; // From Publisher entity
    private String imageUrl;
    private String thumbnailUrl;
    private String contentUrl;

    public MockNewsDTO(MockNews news){
        id=news.getId();
        title=news.getTitle();
        content=news.getContent();
        author=news.getAuthor();
        publisher=news.getPublisher();
        imageUrl=news.getImageUrl();
        thumbnailUrl=news.getThumbnailUrl();
        contentUrl=news.getContentUrl();
    }

}