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
    public ApiResponse<?> signup(@RequestBody SignupRequestDto requestDto) {
        memberService.signup(requestDto);
        return ApiResponse.of("회원 가입 성공");

    }

    @PostMapping("/login")
    public ApiResponse<?> login (@RequestBody LoginRequestDto requestDto, HttpServletResponse response) {
            Member member = memberService.findLocalMember(requestDto.getEmail());

            if (member == null || !passwordEncoder.matches(requestDto.getPassword(), member.getPassword())) {
               throw new CustomException(ReturnCode.NOT_AUTHORIZED);
            }

            MemberDto memberDto = new MemberDto(member);

            String accessToken = jwtUtil.generateAccessToken(memberDto);
            String refreshToken = jwtUtil.generateRefreshToken(memberDto.getEmail());

            jwtUtil.addJwtToCookie(accessToken, response, "accessToken");
            jwtUtil.addJwtToCookie(refreshToken, response, "refreshToken");

            redisService.saveRefreshToken(memberDto.getEmail(), refreshToken);

            return ApiResponse.of("로그인 성공");

    }

    @PostMapping("/logout")
    public ApiResponse<?> logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
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

        return ApiResponse.of("로그아웃 성공");
    }

    @GetMapping("/auth")
    public ApiResponse<MemberDto> authorize(HttpServletRequest request){
        String accessToken = jwtUtil.getJwtFromHeader(request);
        if(accessToken==null) throw new CustomException(ReturnCode.NOT_AUTHORIZED);

        if(!jwtUtil.validateToken(accessToken)) throw new CustomException(ReturnCode.NOT_AUTHORIZED);

        MemberDto member = jwtUtil.getUserInfoFromToken(accessToken);

        return ApiResponse.of(member);
    }

    @GetMapping("/refresh")
    public ApiResponse<?> refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = jwtUtil.resolveRefreshToken(request);

        if(refreshToken==null){
            throw new CustomException(ReturnCode.NOT_AUTHORIZED);
            //return new ResponseEntity<>("RefreshToken 이 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
        }

        if(!jwtUtil.validateToken(refreshToken)){
            throw new CustomException(ReturnCode.NOT_AUTHORIZED);
            //return new ResponseEntity<>("유효하지 않은 RefreshToken 입니다.", HttpStatus.BAD_REQUEST);
        }

        String email = jwtUtil.getMemberEmailFromToken(refreshToken);
        String savedRefreshToken = redisService.getRefreshToken(email);

        if(savedRefreshToken == null || !savedRefreshToken.equals(refreshToken)){
            throw new CustomException(ReturnCode.NOT_AUTHORIZED);
            //return new ResponseEntity<>("RefreshToken 이 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
        }

        Member member = memberService.findByEmail(email);

        MemberDto memberDto = new MemberDto(
             member
        );

        String newAccessToken = jwtUtil.generateAccessToken(memberDto);
        jwtUtil.addJwtToCookie(newAccessToken, response, "accessToken");
        return ApiResponse.of("accessToken 갱신 성공");

    }
}