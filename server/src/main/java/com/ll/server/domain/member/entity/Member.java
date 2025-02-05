package com.ll.server.domain.member.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ll.server.domain.comment.entity.Comment;
import com.ll.server.domain.like.entity.Like;
import com.ll.server.domain.member.MemberRole;
import com.ll.server.domain.notification.entity.Notification;
import com.ll.server.domain.repost.entity.Repost;
import com.ll.server.global.jpa.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Member extends BaseEntity {


    private String email;
    private String password;
    //private String name;
    private String nickname;
    private String profile_url;
    private MemberRole role;
    private String provider;
    private String providerId;

    // jwt 토큰
    //String accessToken;
    private String refreshToken;

    @OneToMany(mappedBy = "member",cascade = CascadeType.ALL,orphanRemoval = true)
    @ToString.Exclude
    @JsonIgnore
    @Builder.Default
    private List<Repost> reposts=new ArrayList<>();

    @OneToMany(mappedBy = "member",cascade = CascadeType.ALL,orphanRemoval = true)
    @ToString.Exclude
    @JsonIgnore
    @Builder.Default
    private List<Comment> comments=new ArrayList<>();

    @OneToMany(mappedBy = "member",cascade = CascadeType.ALL,orphanRemoval = true)
    @ToString.Exclude
    @JsonIgnore
    @Builder.Default
    private List<Like> likes=new ArrayList<>();

    @OneToMany(mappedBy = "member",cascade = CascadeType.ALL,orphanRemoval = true)
    @ToString.Exclude
    @JsonIgnore
    @Builder.Default
    private List<Notification> notifications=new ArrayList<>();

    public void addRepost(Repost repost) {
        reposts.add(repost);
    }

    public void addComment(Comment comment) {
        comments.add(comment);
    }

    public void addLike(Like like) {
        likes.add(like);
    }

    public void addNotification(Notification notification){
        notifications.add(notification);
    }
}
