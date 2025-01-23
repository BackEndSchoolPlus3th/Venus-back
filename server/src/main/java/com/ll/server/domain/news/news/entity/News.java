package com.ll.server.domain.news.news.entity;

import com.ll.server.global.jpa.BaseEntity;
import jakarta.persistence.*;
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
    @Column(columnDefinition = "TEXT")
    private String content;
    @ManyToOne
    @JoinColumn(name = "publisher_id")
    private Publisher publisher;
    private String author;
    private String imageUrl;
    private String thumbnailUrl;
    private String contentUrl;

}
