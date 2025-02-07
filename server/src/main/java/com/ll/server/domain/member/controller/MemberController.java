package com.ll.server.domain.member.controller;

import com.ll.server.domain.member.dto.MemberResponse;
import com.ll.server.domain.member.service.MemberService;
import com.ll.server.global.response.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/info")
    public ApiResponse<MemberResponse> getMemberInfo(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        MemberResponse memberResponse = memberService.getMemberInfo(email);
        return ApiResponse.of(memberResponse);
    }
}
