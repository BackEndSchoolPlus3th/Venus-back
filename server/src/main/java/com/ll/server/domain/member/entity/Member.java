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


    String email;
    String password;
    String name;
    String nickname;
    String profile_url;
    MemberRole role;
    String provider;
    String providerId;

    // jwt 토큰
    String accessToken;
    String refreshToken;

}
