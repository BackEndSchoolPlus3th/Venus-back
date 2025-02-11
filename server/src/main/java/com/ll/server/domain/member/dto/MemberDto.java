package com.ll.server.domain.member.dto;

import com.ll.server.domain.member.entity.Member;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberDto {
    private String email;
    private String nickname;
    private String profileUrl;

    public MemberDto(Member followee) {
        this.email = followee.getEmail();
        this.nickname = followee.getNickname();
        this.profileUrl = followee.getProfileUrl();
    }
}