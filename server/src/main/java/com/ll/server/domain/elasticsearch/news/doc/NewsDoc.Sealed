package com.ll.server.domain.elasticsearch.news.doc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ll.server.domain.news.news.enums.NewsCategory;
import com.ll.server.global.utils.CustomZonedDateTimeConverter;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.ValueConverter;

import java.time.ZonedDateTime;

@Document(indexName = "news")
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"@timestamp"})
public class NewsDoc {

    @Id
    private Long id;

    private String title;

    private String content;
    private String publisher;
    private String author;
    private String imageUrl;
    private String thumbnailUrl;
    private String contentUrl;

    private NewsCategory category;

    private String publishedAt;

    @Field(type = FieldType.Date)
    @ValueConverter(CustomZonedDateTimeConverter.class)
    private ZonedDateTime createDate;

    @Field(type = FieldType.Date)
    @ValueConverter(CustomZonedDateTimeConverter.class)
    private ZonedDateTime modifyDate;

}
