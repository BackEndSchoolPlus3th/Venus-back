package com.ll.server.domain.follow.controller;

import com.ll.server.domain.follow.dto.FollowDTO;
import com.ll.server.domain.follow.dto.FollowRequest;
import com.ll.server.domain.follow.service.FollowService;
import com.ll.server.global.response.enums.ReturnCode;
import com.ll.server.global.response.response.ApiResponse;
import com.ll.server.global.response.response.CustomPage;
import com.ll.server.global.security.util.AuthUtil;
import com.ll.server.global.utils.MyConstant;
import com.ll.server.global.validation.PageLimitSizeValidator;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/follows")
public class ApiV1FollowController {
    private final FollowService followService;

    @Data
    private static class FollowPageRequest{
        private int page=0;
        private int limit=20;
    }

    @PostMapping
    public ApiResponse<FollowDTO> follow(@RequestBody FollowRequest request){
        return ApiResponse.of(followService.save(request.getFollowerId(), AuthUtil.getCurrentMemberId()));
    }

    @GetMapping("/followers")
    public ApiResponse<?> followerList(@RequestParam("nickname") String nickname,
                                       @RequestParam(value = "page",defaultValue = "0")int page,
                                       @RequestParam(value = "size",defaultValue = "20")int size){
        PageLimitSizeValidator.validateSize(page,size, MyConstant.PAGELIMITATION);
        Pageable pageable= PageRequest.of(page,size);
        Page<FollowDTO> result= followService.findFollowers(nickname,pageable);
        return ApiResponse.of(CustomPage.of(result));
    }

    @GetMapping("/followees")
    public ApiResponse<?> followeeList(@RequestParam("nickname") String nickname,
                                       @RequestParam(value = "page",defaultValue = "0")int page,
                                       @RequestParam(value = "size",defaultValue = "20")int size){
        PageLimitSizeValidator.validateSize(page,size, MyConstant.PAGELIMITATION);
        Pageable pageable= PageRequest.of(page,size);
        Page<FollowDTO> result= followService.findFollowees(nickname,pageable);
        return ApiResponse.of(CustomPage.of(result));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> unfollow(@PathVariable Long id){
        followService.delete(id);
        return ApiResponse.of(ReturnCode.SUCCESS);
    }

}
