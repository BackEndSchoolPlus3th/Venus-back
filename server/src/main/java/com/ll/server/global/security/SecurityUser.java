package com.ll.server.global.security;

import com.ll.server.domain.member.entity.Member;
import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class SecurityUser extends User {
    private long id;
    private String email;


    public SecurityUser(Member member,
                        Collection<? extends GrantedAuthority> authorities) {
        super(member.getEmail(), member.getPassword(), authorities);
        this.id = member.getId();
        this.email = member.getEmail();
//        this.role = member.getRole();

    }
    public Authentication genAuthentication() {
        Authentication auth = new UsernamePasswordAuthenticationToken(
                this,
                this.getPassword(),
                this.getAuthorities()
        );
        return auth;
    }
}
