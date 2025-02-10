package com.ll.server.domain.member.dto;

import com.ll.server.domain.member.entity.Member;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberDto {
    private String email;
    private String nickname;
    private String profileUrl;
    private String role;

    public MemberDto(Member member){
        email=member.getEmail();
        nickname=member.getNickname();
        profileUrl=member.getProfileUrl();
        role=member.getRole().name();
    }
}