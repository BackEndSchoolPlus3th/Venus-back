package com.ll.server.domain.mock.follow.controller;

import com.ll.server.domain.mock.follow.dto.FolloweeListResponse;
import com.ll.server.domain.mock.follow.dto.FollowerListResponse;
import com.ll.server.domain.mock.follow.dto.MockFollowDTO;
import com.ll.server.domain.mock.follow.dto.MockFollowRequest;
import com.ll.server.domain.mock.follow.service.MockFollowService;
import com.ll.server.domain.notification.Notify;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/follows")
public class ApiV1MockFollowController {
    private final MockFollowService mockFollowService;

    @PostMapping
    @Notify
    public MockFollowDTO follow(@RequestBody MockFollowRequest request){
        return mockFollowService.save(request.getFollowerId(),request.getFolloweeId());
    }

    @GetMapping("/followers")
    public FollowerListResponse followerList(@RequestParam("nickname") String nickname){
        return mockFollowService.findFollowers(nickname);
    }

    @GetMapping("/followees")
    public FolloweeListResponse followeeList(@RequestParam("nickname") String nickname){
        return mockFollowService.findFollowees(nickname);
    }

    @DeleteMapping("/{id}")
    public String unfollow(@PathVariable Long id){
        return mockFollowService.delete(id);
    }

}
