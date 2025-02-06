package com.ll.server.domain.member.controller;

import com.ll.server.domain.member.dto.MemberDto;
import com.ll.server.domain.member.dto.MemberLogin;
import com.ll.server.domain.member.dto.MemberRequest;
import com.ll.server.domain.member.entity.Member;
import com.ll.server.domain.member.service.MemberService;
import com.ll.server.global.jwt.JwtProvider;
import com.ll.server.global.rsData.RsData;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class ApiV1MemberController {
    private final AuthenticationManager authenticationManager;
    private final MemberService memberService;
    private final JwtProvider jwtProvider;

    @PostMapping("/signup")
    public String join(@Valid @RequestBody MemberRequest memberRequest) {
        Member member = memberService.join(
                memberRequest.getEmail(),
                memberRequest.getPassword(),
                memberRequest.getRole(),
                //memberRequest.getName(),
                memberRequest.getNickname(),
                memberRequest.getProviderId());
        System.out.println("회원가입 성공"+ member.getNickname());
        return "signup finished";
    }

    @PostMapping("/login")
    public RsData<Void> login(@Valid @RequestBody MemberLogin memberLogin, HttpServletResponse response) {
        // 1. AuthenticationManager를 사용하여 인증 수행
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(memberLogin.getEmail(), memberLogin.getPassword())
        );

        //  2. SecurityContextHolder에 인증 정보 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. JWT 토큰 생성 및 쿠키 저장
        Member member = memberService.getMember(memberLogin.getEmail());
        String token = jwtProvider.genAccessToken(member);

        Cookie accessTokenCookie = new Cookie("accessToken", token);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(60 * 60);
        response.addCookie(accessTokenCookie);

        // 4. RefreshToken 저장
        String refreshToken = member.getRefreshToken();
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(60 * 60);
        response.addCookie(refreshTokenCookie);

        return new RsData<>("200", "Login Success");
    }




    @GetMapping("/logout")
    public RsData<Void> logout(HttpServletResponse response) {
        // 응답 데이터에 accessToken 이름으로 토큰을 발급
        Cookie cookie = new Cookie("accessToken", null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        Cookie refreshTokenCookie  = new Cookie("refreshToken", null);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(0);
        response.addCookie(refreshTokenCookie);

        SecurityContextHolder.clearContext(); // 현재 요청에서 인증 정보 제거
        return new RsData<>("200", "로그아웃에 성공하였습니다.");
    }

    // 마이페이지
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public RsData<MemberDto> me(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        String accessToken = "";
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("accessToken")) {
                accessToken = cookie.getValue();
            }
        }
        Map<String, Object> claims = jwtProvider.getClaims(accessToken);
        System.out.println(claims);
        String email = (String) claims.get("email");
        System.out.println(email);



        Member member = this.memberService.getMember(email);

        return new RsData("200", "회원정보 조회 성공",
                new MemberDto(member));
    }
}
