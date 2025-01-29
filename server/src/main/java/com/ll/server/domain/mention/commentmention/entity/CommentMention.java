package com.ll.server.domain.mention.commentmention.entity;


import com.ll.server.domain.mock.comment.entity.MockComment;
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
public class CommentMention extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    private MockComment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    private MockUser user;
}
