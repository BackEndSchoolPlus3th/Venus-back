package com.ll.server.domain.elasticsearch.repost.doc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ll.server.global.utils.CustomZonedDateTimeConverter;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.ValueConverter;

import java.time.ZonedDateTime;

@Document(indexName = "repost")
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"@timestamp"})
public class RepostDoc {

    @Id
    private Long id;

    private Long newsId;

    private Long userId;

    private String content;

    @Field(type = FieldType.Date)
    @ValueConverter(CustomZonedDateTimeConverter.class)
    private ZonedDateTime deletedAt;

    @Field(type = FieldType.Date)
    @ValueConverter(CustomZonedDateTimeConverter.class)
    private ZonedDateTime createDate;

    @Field(type = FieldType.Date)
    @ValueConverter(CustomZonedDateTimeConverter.class)
    private ZonedDateTime modifyDate;

}
