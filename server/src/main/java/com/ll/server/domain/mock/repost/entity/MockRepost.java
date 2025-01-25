package com.ll.server.domain.mock.repost.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ll.server.domain.mention.repostmention.entity.RepostMention;
import com.ll.server.domain.mock.comment.entity.MockComment;
import com.ll.server.domain.mock.like.entity.MockLike;
import com.ll.server.domain.mock.user.entity.MockUser;
import com.ll.server.domain.news.news.entity.News;
import com.ll.server.global.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString
public class MockRepost extends BaseEntity {
    @ManyToOne(fetch=FetchType.LAZY)
    private News news;

    @ManyToOne(fetch = FetchType.LAZY)
    private MockUser user;

    @Column(columnDefinition = "TEXT")
    private String content;
    private Boolean pinned;

    @Setter
    @Builder.Default
    private LocalDateTime deletedAt=null;

    @OneToMany(mappedBy = "repost",cascade = CascadeType.ALL,orphanRemoval = true)
    @ToString.Exclude
    @Builder.Default
    @JsonIgnore
    private List<RepostMention> mentions=new ArrayList<>();

    @OneToMany(mappedBy = "repost",cascade = CascadeType.ALL,orphanRemoval = true)
    @ToString.Exclude
    @Builder.Default
    @JsonIgnore
    private List<MockLike> likes=new ArrayList<>();

    @OneToMany(mappedBy = "repost",cascade = CascadeType.ALL,orphanRemoval = true)
    @ToString.Exclude
    @Builder.Default
    @JsonIgnore
    private List<MockComment> comments=new ArrayList<>();

    public void addMention(MockUser user){
        RepostMention mention= RepostMention.builder()
                .repost(this)
                .user(user)
                .build();

        this.mentions.add(mention);
    }


    public MockComment addComment(MockUser user, List<MockUser> mentionedUsers, String content){
        MockComment comment=MockComment.builder()
                .repost(this)
                .content(content)
                .user(user)
                .build();

        for(MockUser mentionedUser:mentionedUsers){
            comment.addMention(mentionedUser);
        }

        comments.add(comment);

        return comment;
    }

    public MockLike addLike(MockUser user){
        MockLike like=MockLike.builder()
                .repost(this)
                .deleted(false)
                .user(user)
                .build();

        likes.add(like);

        return like;

    }


    public void deleteComments() {
        comments.forEach(comment -> comment.setDeletedAt(LocalDateTime.now()));
    }

    public void deleteLikes(){
        likes.forEach(like -> like.setDeleted(true));
    }
}
