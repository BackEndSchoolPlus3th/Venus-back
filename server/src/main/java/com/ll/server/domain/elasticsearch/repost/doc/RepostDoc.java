package com.ll.server.domain.elasticsearch.repost.doc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ll.server.global.utils.CustomZonedDateTimeConverter;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.ZonedDateTime;

@Document(indexName = "repost")
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Mapping(mappingPath = "repostDoc/repostMapping.json")
@Setting(settingPath = "repostDoc/repostSetting.json")
public class RepostDoc {

    @Id
    private Long id;

    @JsonProperty("news_id")
    private Long newsId;

    @JsonProperty("user_id")
    private Long userId;

    private String content;

    @JsonProperty("image_url")
    private String imageUrl;


    @Field(type = FieldType.Date)
    @ValueConverter(CustomZonedDateTimeConverter.class)
    @JsonProperty("deleted_at")
    private ZonedDateTime deletedAt;

    @Field(type = FieldType.Date)
    @ValueConverter(CustomZonedDateTimeConverter.class)
    @JsonProperty("create_date")
    private ZonedDateTime createDate;

    @Field(type = FieldType.Date)
    @ValueConverter(CustomZonedDateTimeConverter.class)
    @JsonProperty("modify_date")
    private ZonedDateTime modifyDate;

}
