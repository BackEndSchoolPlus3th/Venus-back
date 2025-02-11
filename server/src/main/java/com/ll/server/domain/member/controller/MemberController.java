package com.ll.server.domain.member.controller;

import com.ll.server.domain.member.auth.dto.LoginRequestDto;
import com.ll.server.domain.member.auth.dto.SignupRequestDto;
import com.ll.server.domain.member.dto.MemberDto;
import com.ll.server.domain.member.entity.Member;
import com.ll.server.domain.member.service.MemberService;
import com.ll.server.global.redis.RedisService;
import com.ll.server.global.response.enums.ReturnCode;
import com.ll.server.global.response.exception.CustomException;
import com.ll.server.global.response.response.ApiResponse;
import com.ll.server.global.security.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

@Slf4j(topic = "MemberController")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {

    private final MemberService memberService;
    private final BCryptPasswordEncoder passwordEncoder; // 로그인 시 비밀번호 검증에 필요
    private final JwtUtil jwtUtil;
    private final RedisService redisService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequestDto requestDto) {
        try {
            memberService.signup(requestDto);
            return new ResponseEntity<>("회원가입 성공", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login (@RequestBody LoginRequestDto requestDto, HttpServletResponse response) {
        try {
            Member member = memberService.findByEmail(requestDto.getEmail());

            if (member == null || !passwordEncoder.matches(requestDto.getPassword(), member.getPassword())) {
                return new ResponseEntity<>("비밀번호나 아이디가 다릅니다..", HttpStatus.UNAUTHORIZED);
            }

            MemberDto memberDto = new MemberDto(member);

            String accessToken = jwtUtil.generateAccessToken(memberDto);
            String refreshToken = jwtUtil.generateRefreshToken(memberDto.getEmail());

            jwtUtil.addJwtToCookie(accessToken, response, "accessToken");
            jwtUtil.addJwtToCookie(refreshToken, response, "refreshToken");

            redisService.saveRefreshToken(memberDto.getEmail(), refreshToken);

            return new ResponseEntity<>("로그인 성공", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // 1. Spring Security Context Logout 처리
        if (authentication == null) {
            throw new CustomException(ReturnCode.INTERNAL_ERROR);
        }

        new SecurityContextLogoutHandler().logout(request, response, authentication);
        // 2. JWT 쿠키 삭제
        jwtUtil.deleteJwtFromCookie(response, "accessToken");
        jwtUtil.deleteJwtFromCookie(response, "refreshToken");

        // 3. RefreshToken 삭제
        String refreshToken = jwtUtil.resolveRefreshToken(request);

        if (refreshToken != null) {
            String email = jwtUtil.getMemberEmailFromToken(refreshToken);
            redisService.deleteRefreshToken(email);
        }

        return new ResponseEntity<>("로그아웃 성공", HttpStatus.OK);
    }

    @GetMapping("/auth")
    public ApiResponse<MemberDto> authorizeByCookie(HttpServletRequest request){
        String accessToken = jwtUtil.resolveAccessToken(request);
        if(accessToken==null) throw new CustomException(ReturnCode.NOT_AUTHORIZED);

        if(!jwtUtil.validateToken(accessToken)) throw new CustomException(ReturnCode.NOT_AUTHORIZED);

        MemberDto member = jwtUtil.getUserInfoFromToken(accessToken);

        return ApiResponse.of(member);
    }

    @GetMapping("/refresh")
    public ResponseEntity<String> refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = jwtUtil.resolveRefreshToken(request);

        if(refreshToken==null){
            return new ResponseEntity<>("RefreshToken 이 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
        }

        if(!jwtUtil.validateToken(refreshToken)){
            return new ResponseEntity<>("유효하지 않은 RefreshToken 입니다.", HttpStatus.BAD_REQUEST);
        }

        String email = jwtUtil.getMemberEmailFromToken(refreshToken);
        String savedRefreshToken = redisService.getRefreshToken(email);

        if(savedRefreshToken == null || !savedRefreshToken.equals(refreshToken)){
            return new ResponseEntity<>("RefreshToken 이 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
        }

        Member member = memberService.findByEmail(email);

        MemberDto memberDto = new MemberDto(
             member
        );

        String newAccessToken = jwtUtil.generateAccessToken(memberDto);
        jwtUtil.addJwtToCookie(newAccessToken, response, "accessToken");
        return new ResponseEntity<>("AccessToken 갱신 성공", HttpStatus.OK);

    }
}