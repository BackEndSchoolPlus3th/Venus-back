package com.ll.server.domain.mention.repostmention.entity;


import com.ll.server.domain.mock.user.entity.MockUser;
import com.ll.server.domain.repost.entity.Repost;
import com.ll.server.global.jpa.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RepostMention extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    private Repost repost;
    @ManyToOne(fetch = FetchType.LAZY)
    private MockUser user;

}
