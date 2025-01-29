package com.ll.server.domain.news.news.entity;

import com.ll.server.domain.news.news.enums.NewsCategory;
import com.ll.server.global.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@Table(name = "news")
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class News extends BaseEntity {
    private String title;
    @Column(columnDefinition = "TEXT")
    private String content;
    private String publisher;
    private String author;
    private String imageUrl;
    private String thumbnailUrl;
    private String contentUrl;
    @Enumerated(EnumType.STRING)
    private NewsCategory category;
    private String publishedAt;
}
