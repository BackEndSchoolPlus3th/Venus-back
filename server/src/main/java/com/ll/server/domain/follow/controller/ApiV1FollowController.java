package com.ll.server.domain.follow.controller;

import com.ll.server.domain.follow.dto.FollowDTO;
import com.ll.server.domain.follow.dto.FollowRequest;
import com.ll.server.domain.follow.dto.FolloweeCountDTO;
import com.ll.server.domain.follow.dto.FollowerCountDTO;
import com.ll.server.domain.follow.service.FollowService;
import com.ll.server.domain.member.dto.MemberDto;
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
    public ApiResponse<?> followerList(@RequestParam("nickname") String nickname, //프론트에서 뭘 넘기느냐에 따라 다름.
                                       @RequestParam(value = "page",defaultValue = "0")int page,
                                       @RequestParam(value = "size",defaultValue = "20")int size){
        PageLimitSizeValidator.validateSize(page,size, MyConstant.PAGELIMITATION);
        Pageable pageable= PageRequest.of(page,size);
        Page<MemberDto> result= followService.findFollowers(nickname,pageable);
        return ApiResponse.of(CustomPage.of(result));
    }

    @GetMapping("/followercount")
    public ApiResponse<?> followerCount(@RequestParam("nickname") String nickname){
        long count = followService.getFollowerCount(nickname);

        return ApiResponse.of(new FollowerCountDTO(count));
    }

    @GetMapping("/followeecount")
    public ApiResponse<?> followeeCount(@RequestParam("nickname") String nickname){
        long count = followService.getFolloweeCount(nickname);

        return ApiResponse.of(new FolloweeCountDTO(count));
    }

    @GetMapping("/followers/infinityTest")
    public ApiResponse<?> followerListInfinity(@RequestParam("nickname") String nickname,
                                               @RequestParam(value = "lastId",required = false) Long lastId,
                                               @RequestParam(value = "size",defaultValue = "20")int size){
        if(lastId==null){
            return ApiResponse.of(followService.firstGetFollowersInfinity(nickname,size));
        }

        return ApiResponse.of(followService.afterGetFollowersInfinity(nickname,size,lastId));
    }

    @GetMapping("/followees")
    public ApiResponse<?> followeeList(@RequestParam("nickname") String nickname,
                                       @RequestParam(value = "page",defaultValue = "0")int page,
                                       @RequestParam(value = "size",defaultValue = "20")int size){
        PageLimitSizeValidator.validateSize(page,size, MyConstant.PAGELIMITATION);
        Pageable pageable= PageRequest.of(page,size);
        Page<MemberDto> result= followService.findFollowees(nickname,pageable);
        return ApiResponse.of(CustomPage.of(result));
    }

    @GetMapping("/followees/infinityTest")
    public ApiResponse<?> followeeListInfinity(@RequestParam("nickname") String nickname,
                                       @RequestParam(value = "lastId",required = false) Long lastId,
                                               @RequestParam(value = "size",defaultValue = "20")int size){
        if(lastId==null){
            return ApiResponse.of(followService.firstGetFolloweesInfinity(nickname,size));
        }

        return ApiResponse.of(followService.afterGetFolloweesInfinity(nickname,size,lastId));
    }


    @DeleteMapping("/{id}")
    public ApiResponse<String> unfollow(@PathVariable Long id){
        followService.delete(id);
        return ApiResponse.of(ReturnCode.SUCCESS);
    }

}
