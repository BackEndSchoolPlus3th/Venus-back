package com.ll.server.domain.member.auth.security.oauth2;

import com.ll.server.domain.member.entity.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {
    private final Member member;
    private final Map<String, Object> attributes;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomOAuth2User(Member member, Map<String, Object> attributes, Collection<? extends GrantedAuthority> authorities) {
        this.member = member;
        this.attributes = attributes;
        this.authorities = authorities;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getName() {
        return member.getEmail();
    }

    public Member getMember() {
        return member;
    }
}
