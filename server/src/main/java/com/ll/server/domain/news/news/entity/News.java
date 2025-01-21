package com.ll.server.domain.news.news.entity;

import com.ll.server.global.jpa.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Table(name = "news")
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class News extends BaseEntity {
    private String title;
    private String content;
    @ManyToOne
    private Publisher publisher;
    private String author;
    private String imageUrl;
    private String thumbnailUrl;
    private String contentUrl;

}
