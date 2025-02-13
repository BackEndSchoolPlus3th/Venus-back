package com.ll.server.domain.saved.entity;


import com.ll.server.domain.member.entity.Member;
import com.ll.server.domain.news.news.entity.News;
import com.ll.server.global.jpa.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Saved extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private News news;

    @Builder.Default
    @Setter
    private Boolean deleted=false;

    public void delete(){
        this.deleted=true;
    }
}
