package com.ll.server.domain.member.dto;

import com.ll.server.domain.member.entity.Member;
import lombok.Getter;

@Getter
public class MemberDto {
    private String nickname;
    private String email;
    private String providerId;

    public MemberDto(Member member) {
        this.nickname = member.getNickname();
        this.email = member.getEmail();
        this.providerId = member.getProviderId();

    }
}