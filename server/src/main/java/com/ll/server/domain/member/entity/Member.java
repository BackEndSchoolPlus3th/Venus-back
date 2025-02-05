package com.ll.server.domain.member.entity;

import com.ll.server.domain.member.MemberRole;
import com.ll.server.global.jpa.BaseEntity;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

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

//    @OneToMany(mappedBy = "member",cascade = CascadeType.ALL,orphanRemoval = true)
//    @ToString.Exclude
//    @JsonIgnore
//    @Builder.Default
//    private List<Repost> reposts=new ArrayList<>();
//
//    @OneToMany(mappedBy = "member",cascade = CascadeType.ALL,orphanRemoval = true)
//    @ToString.Exclude
//    @JsonIgnore
//    @Builder.Default
//    private List<Comment> comments=new ArrayList<>();
//
//    @OneToMany(mappedBy = "member",cascade = CascadeType.ALL,orphanRemoval = true)
//    @ToString.Exclude
//    @JsonIgnore
//    @Builder.Default
//    private List<Notification> notifications=new ArrayList<>();

}
