package com.ll.server.domain.news.news.entity;

import com.ll.server.domain.news.news.enums.Country;
import com.ll.server.global.jpa.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Table(name = "publisher")
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class Publisher extends BaseEntity {
    private String publisher;
    private Country countryName;
}
