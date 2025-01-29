package com.ll.server.domain.mention.repostmention.entity;


import com.ll.server.domain.mock.repost.entity.MockRepost;
import com.ll.server.global.jpa.BaseEntity;
import com.ll.server.domain.mock.user.entity.MockUser;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RepostMention extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    private MockRepost repost;
    @ManyToOne(fetch = FetchType.LAZY)
    private MockUser user;

}
