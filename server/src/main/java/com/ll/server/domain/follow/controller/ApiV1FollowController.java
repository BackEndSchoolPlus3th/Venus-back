package com.ll.server.domain.follow.controller;

import com.ll.server.domain.follow.dto.FolloweeListResponse;
import com.ll.server.domain.follow.dto.FollowDTO;
import com.ll.server.domain.follow.dto.FollowRequest;
import com.ll.server.domain.follow.service.FollowService;
import com.ll.server.domain.follow.dto.FollowerListResponse;
import com.ll.server.domain.notification.Notify;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/follows")
public class ApiV1FollowController {
    private final FollowService followService;

    @PostMapping
    @Notify
    public FollowDTO follow(@RequestBody FollowRequest request){
        return followService.save(request.getFollowerId(),request.getFolloweeId());
    }

    @GetMapping("/followers")
    public FollowerListResponse followerList(@RequestParam("nickname") String nickname){
        return followService.findFollowers(nickname);
    }

    @GetMapping("/followees")
    public FolloweeListResponse followeeList(@RequestParam("nickname") String nickname){
        return followService.findFollowees(nickname);
    }

    @DeleteMapping("/{id}")
    public String unfollow(@PathVariable Long id){
        return followService.delete(id);
    }

}
