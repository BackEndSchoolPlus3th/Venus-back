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

    public MemberDto(Member followee) {
        this.email = followee.getEmail();
        this.nickname = followee.getNickname();
        this.profileUrl = followee.getProfileUrl();
        this.role = followee.getRole().name();
    }
}