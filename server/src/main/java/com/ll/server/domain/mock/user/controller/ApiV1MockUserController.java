package com.ll.server.domain.mock.user.controller;


import com.ll.server.domain.mock.user.dto.MockUserLoginRequest;
import com.ll.server.domain.mock.user.dto.MockUserSignupRequest;
import com.ll.server.domain.mock.user.dto.UserProfile;
import com.ll.server.domain.mock.user.entity.MockUser;
import com.ll.server.domain.mock.user.service.MockUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class ApiV1MockUserController {
    private final MockUserService userService;

    @PostMapping("/signup")
    public String signup(@RequestBody MockUserSignupRequest request){
        MockUser user=userService.signup(request);

        if(user==null) return "회원 가입 실패";

        return "회원 가입 성공";
    }

    @PostMapping("/login")
    public String login(@RequestBody MockUserLoginRequest request){
        MockUser user=userService.login(request);

        if(user==null) return "로그인 실패";

        return "로그인 성공";
    }

    @GetMapping("/{userId}")
    public UserProfile getProfile(@PathVariable("userId") Long userId){
        return userService.getProfile(userId);
    }



}
