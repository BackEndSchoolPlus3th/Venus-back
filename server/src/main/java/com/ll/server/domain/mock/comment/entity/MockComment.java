package com.ll.server.domain.mock.comment.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ll.server.domain.mention.commentmention.entity.CommentMention;
import com.ll.server.domain.mock.repost.entity.MockRepost;
import com.ll.server.domain.mock.user.entity.MockUser;
import com.ll.server.global.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
public class MockComment extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    private MockRepost repost;

    @ManyToOne(fetch = FetchType.LAZY)
    private MockUser user;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Builder.Default
    private LocalDateTime deletedAt=null;

    @OneToMany(mappedBy = "comment",cascade = CascadeType.ALL,orphanRemoval = true)
    @ToString.Exclude
    @Builder.Default
    @JsonIgnore
    List<CommentMention> mentions=new ArrayList<>();

    public void addMention(MockUser user){
        CommentMention mention=CommentMention
                .builder()
                .comment(this)
                .user(user)
                .build();

        mentions.add(mention);
    }
}
