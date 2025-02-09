package com.ll.server.global.security.filter;

import com.ll.server.domain.member.dto.MemberDto;
import com.ll.server.domain.member.entity.Member;
import com.ll.server.domain.member.repository.MemberRepository;
import com.ll.server.global.security.custom.CustomUserDetails;
import com.ll.server.global.security.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// JWT 를 검증하고, 인증된 사용자의 정보를 SecurityContext 에 저장하는 역할
@Slf4j(topic = "JwtAuthorizationFilter")
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;

    @Override
    protected void doFilterInternal (HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = jwtUtil.getJwtFromHeader(request);

        if (StringUtils.hasText(token)) {
            if (!jwtUtil.validateToken(token)) {
                log.error("유효하지 않은 JWT 토큰입니다.");
                filterChain.doFilter(request, response);
                return;
            }

            MemberDto memberDto = jwtUtil.getUserInfoFromToken(token);

            try {
                setAuthentication(memberDto.getEmail());
            } catch (Exception e) {
                log.error("인증 처리 실패: {}", e.getMessage());
                filterChain.doFilter(request, response);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    public void setAuthentication (String email) {
        Member member = memberRepository.findMemberByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("이메일을 찾을 수 없습니다: " + email));
        CustomUserDetails userDetails = new CustomUserDetails(member);

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("Authentication 을 홀더에 저장했음");
    }
}
