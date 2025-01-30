package com.ll.server.domain.mock.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ll.server.domain.comment.entity.Comment;
import com.ll.server.domain.mock.user.MockRole;
import com.ll.server.domain.notification.entity.Notification;
import com.ll.server.domain.repost.entity.Repost;
import com.ll.server.global.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MockUser extends BaseEntity {
    private String email; //이게 id 역할을 한다고 가정.
    private String password;
    private String nickname;
    private String profileUrl;
    @Enumerated(value = EnumType.STRING)
    private MockRole role;
    private String provider; //oauth2 연동한 사이트
    private String providerId; //고유 식별 id
    private String refreshToken;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,orphanRemoval = true)
    @ToString.Exclude
    @JsonIgnore
    @Builder.Default
    private List<Repost> reposts=new ArrayList<>();

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,orphanRemoval = true)
    @ToString.Exclude
    @JsonIgnore
    @Builder.Default
    private List<Comment> comments=new ArrayList<>();

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,orphanRemoval = true)
    @ToString.Exclude
    @JsonIgnore
    @Builder.Default
    private List<Notification> notifications=new ArrayList<>();

}
