package com.ll.server.domain.like.entity;

import com.ll.server.domain.member.entity.Member;
import com.ll.server.domain.repost.entity.Repost;
import com.ll.server.global.jpa.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "\"like\"")
public class Like extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    private Repost repost;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Builder.Default
    @Setter
    private Boolean deleted = false;
}
