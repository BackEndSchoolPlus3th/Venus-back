package com.ll.server.domain.elasticsearch.news.doc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ll.server.domain.news.news.enums.NewsCategory;
import com.ll.server.global.utils.CustomZonedDateTimeConverter;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Document(indexName = "news")
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"@timestamp"})
@Mapping(mappingPath = "newsDoc/newsMapping.json")
@Setting(settingPath = "newsDoc/newsSetting.json")
public class NewsDoc {

    @Id
    private Long id;

    private String title;

    private String content;
    private String publisher;
    private String author;
    @JsonProperty("image_url")
    private String imageUrl;
    @JsonProperty("thumbnail_url")
    private String thumbnailUrl;
    @JsonProperty("content_url")
    private String contentUrl;

    private NewsCategory category;

    @JsonProperty("published_at")
    @Field(type = FieldType.Date, format = DateFormat.strict_date_time)
    @ValueConverter(CustomZonedDateTimeConverter.class)
    private LocalDateTime publishedAt;

    @Field(type = FieldType.Date, format = DateFormat.strict_date_time)
    @ValueConverter(CustomZonedDateTimeConverter.class)
    @JsonProperty("create_date")
    private ZonedDateTime createDate;

    @Field(type = FieldType.Date, format = DateFormat.strict_date_time)
    @ValueConverter(CustomZonedDateTimeConverter.class)
    @JsonProperty("modify_date")
    private ZonedDateTime modifyDate;

    @Field(type = FieldType.Date)
    @ValueConverter(CustomZonedDateTimeConverter.class)
    @JsonProperty("deleted_at")
    private ZonedDateTime deletedAt;

}
