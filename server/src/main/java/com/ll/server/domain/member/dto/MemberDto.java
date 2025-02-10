package com.ll.server.domain.member.dto;

import com.ll.server.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MemberDto {
    private String email;
    private String nickname;
    private String profileUrl;
    private String role;

    public MemberDto(Member follower) {
        this.email = follower.getEmail();
        this.nickname = follower.getNickname();
        this.profileUrl = follower.getProfileUrl();
        this.role = String.valueOf(follower.getRole());
    }
}