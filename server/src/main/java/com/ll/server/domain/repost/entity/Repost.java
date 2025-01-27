package com.ll.server.domain.repost.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ll.server.domain.comment.entity.Comment;
import com.ll.server.domain.like.entity.Like;
import com.ll.server.domain.mock.user.entity.MockUser;
import com.ll.server.domain.news.news.entity.News;
import com.ll.server.global.jpa.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Repost extends BaseEntity {

    @NotBlank
    @ManyToOne(fetch= FetchType.LAZY)
    private News news;

    @NotBlank
    @ManyToOne(fetch = FetchType.LAZY)
    private MockUser user;

    @NotBlank
    @Column(columnDefinition = "TEXT")
    private String content;

    @Builder.Default
    private LocalDateTime deletedAt=null;

//    @OneToMany(mappedBy = "repost", cascade = CascadeType.ALL,orphanRemoval = true)
//    @ToString.Exclude
//    @Builder.Default
//    @JsonIgnore
//    private List<RepostMention> mentions=new ArrayList<>();

    @OneToMany(mappedBy = "repost", cascade = CascadeType.ALL,orphanRemoval = true)
    @ToString.Exclude
    @Builder.Default
    @JsonIgnore
    private List<Like> likes=new ArrayList<>();

    @OneToMany(mappedBy = "repost", cascade = CascadeType.ALL,orphanRemoval = true)
    @ToString.Exclude
    @Builder.Default
    @JsonIgnore
    private List<Comment> comments=new ArrayList<>();

    private Boolean pinned;


    public Comment addComment(MockUser user, List<MockUser> mentionedUsers, String content){
        Comment comment = Comment.builder()
                .repost(this)
                .content(content)
                .user(user)
                .build();

//        for(MockUser mentionedUser:mentionedUsers){
//            comment.addMention(mentionedUser);
//        }

        comments.add(comment);

        return comment;
    }

    public Like addLike(MockUser user){
        Like like = Like.builder()
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
