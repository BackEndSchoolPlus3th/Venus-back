package com.ll.server.global.security.filter;

import com.ll.server.global.security.custom.CustomUserDetailsService;
import com.ll.server.global.security.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

// JWT 를 검증하고, 인증된 사용자의 정보를 SecurityContext 에 저장하는 역할
@Slf4j(topic = "JwtAuthorizationFilter")
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        // Skip filtering for these paths
        return path.startsWith("/h2-console") || path.startsWith("/api/v1/member/signup") || path.startsWith("/api/v1/member/login") || path.startsWith("/oauth2/**") || path.startsWith("/api/v1/member/auth");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = jwtUtil.getJwtFromHeader(request);
        if (token == null) {
            log.warn("Authorization에 없음");
            token = jwtUtil.resolveToken(request);
        }

        if (StringUtils.hasText(token)) {
            if (!jwtUtil.validateToken(token)) {
                log.warn("유효하지 않은 JWT 토큰입니다.");
                filterChain.doFilter(request, response);
                return;
            }

            String email = jwtUtil.getMemberEmailFromToken(token);
            log.info("정상적으로 사용자 정보를 토큰으로부터 가져왔습니다. Email: {}", email);

            try {
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("SecurityContext 에 인증 정보 설정 완료: {}", authentication);
            } catch (Exception e) {
                log.error("인증 처리 실패: {}", e.getMessage());
                jwtUtil.deleteJwtFromCookie(response,"accessToken");
                jwtUtil.deleteJwtFromCookie(response,"refreshToken");
                SecurityContextHolder.clearContext();
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                response.getWriter().write("{\"message\": \"인증 실패\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

}
