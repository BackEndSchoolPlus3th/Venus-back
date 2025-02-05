package com.ll.server.domain.member.entity;

import com.ll.server.domain.member.enums.MemberRole;
import com.ll.server.domain.member.enums.Provider;
import com.ll.server.global.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "members")
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class Member extends BaseEntity implements UserDetails {
    @Column(unique = true, nullable = false)
    private String email; //이게 id 역할
    @Column(nullable = false)
    private String password;
    @Column(unique = true, nullable = false)
    private String nickname;
    @Column
    private String profileUrl;
    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private MemberRole role;
    @Enumerated(value = EnumType.STRING)
    private Provider provider; // 로그인 타입
    @Column
    private String providerId; // 소셜로그인 시 ID

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

