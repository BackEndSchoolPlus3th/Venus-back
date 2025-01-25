package com.ll.server.domain.mock.repost.controller;

import com.ll.server.domain.mock.comment.dto.MockCommentDTO;
import com.ll.server.domain.mock.comment.dto.MockCommentModifyRequest;
import com.ll.server.domain.mock.comment.dto.MockCommentWriteRequest;
import com.ll.server.domain.mock.like.dto.MockLikeDTO;
import com.ll.server.domain.mock.like.dto.MockLikeResponse;
import com.ll.server.domain.mock.repost.dto.MockRepostDTO;
import com.ll.server.domain.mock.repost.dto.MockRepostWriteRequest;
import com.ll.server.domain.mock.repost.service.MockRepostService;
import com.ll.server.domain.notification.Notify;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reposts")
public class ApiV1MockRepostController {
    private final MockRepostService repostService;

    //repost 영역
    @GetMapping
    public List<MockRepostDTO> getAllRepost(){
        return repostService.findAll();
    }

    @GetMapping("/{id}")
    public MockRepostDTO getRepost(@PathVariable("id") Long id){
        return repostService.findById(id);
    }

    @DeleteMapping("/{id}")
    public String deletePost(@PathVariable("id") Long id){return repostService.delete(id);}

    @PostMapping
    @Notify
    public MockRepostDTO write(@RequestBody MockRepostWriteRequest request){
        return repostService.save(request);
    }

    //comment 영역
    @GetMapping("/{repostId}/comments")
    public List<MockCommentDTO> getAllComment(@PathVariable("repostId") Long postId){
        return repostService.getAllComment(postId);
    }

    @DeleteMapping("/{repostId}/comments/{commentId}")
    public String deleteComment(@PathVariable("repostId")Long postId,
                                @PathVariable("commentId")Long commentId){
        return repostService.deleteComment(postId,commentId);
    }

    @PatchMapping("/{repostId}/comments/{commentId}")
    public MockCommentDTO modifyComment(@PathVariable("repostId")Long postId,
                                        @PathVariable("commentId")Long commentId,
                                        @RequestBody MockCommentModifyRequest request) {
        return repostService.modifyComment(postId, commentId,request.getContent());
    }

    @PostMapping("/{repostId}/comments")
    @Notify
    public MockCommentDTO addComment(@PathVariable("repostId") Long postId,
                                     @RequestBody MockCommentWriteRequest request){
        return repostService.addComment(postId, request);
    }

    //like 영역
    @GetMapping("/{repostId}/likes")
    public MockLikeResponse getLikes(@PathVariable("repostId") Long repostId){
        List<MockLikeDTO> likes=repostService.getAllLike(repostId);
        return new MockLikeResponse(likes);
    }

    @DeleteMapping("/{repostId}/likes/{userId}")
    public String deleteLike(@PathVariable("repostId") Long repostId,
                             @PathVariable("userId") Long userId){
        return repostService.deleteLike(repostId, userId);
    }

    @PostMapping("/{repostId}/likes/{userId}")
    @Notify
    public MockLikeDTO markLike(@PathVariable("repostId") Long repostId,
                           @PathVariable("userId") Long userId){
        return repostService.markLike(repostId,userId);
    }


}
