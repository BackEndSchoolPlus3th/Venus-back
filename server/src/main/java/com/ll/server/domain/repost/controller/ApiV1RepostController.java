package com.ll.server.domain.repost.controller;

import com.ll.server.domain.comment.dto.CommentDTO;
import com.ll.server.domain.comment.dto.CommentModifyRequest;
import com.ll.server.domain.comment.dto.CommentResponse;
import com.ll.server.domain.comment.dto.CommentWriteRequest;
import com.ll.server.domain.elasticsearch.repost.service.RepostDocService;
import com.ll.server.domain.like.dto.LikeDTO;
import com.ll.server.domain.like.dto.LikeResponse;
import com.ll.server.domain.repost.dto.RepostDTO;
import com.ll.server.domain.repost.dto.RepostWriteRequest;
import com.ll.server.domain.repost.service.RepostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reposts")
public class ApiV1RepostController {
    private final RepostService repostService;
    private final RepostDocService repostDocService;

    //repost 영역
    @GetMapping
    public List<RepostDTO> getAllRepost(){
        return repostService.findAll();
    }

    @GetMapping("/{id}")
    public RepostDTO getRepost(@PathVariable("id") Long id){
        return repostService.findById(id);
    }

    @DeleteMapping("/{id}")
    public String deletePost(@PathVariable("id") Long id){return repostService.delete(id);}

    @PostMapping
    public RepostDTO write(@RequestBody RepostWriteRequest request){
        return repostService.save(request);
    }

    //comment 영역
    @GetMapping("/{repostId}/comments")
    public CommentResponse getAllComment(@PathVariable("repostId") Long postId){
        return new CommentResponse(repostService.getAllComment(postId));
    }

    @DeleteMapping("/{repostId}/comments/{commentId}")
    public String deleteComment(@PathVariable("repostId")Long postId,
                                @PathVariable("commentId")Long commentId){
        return repostService.deleteComment(postId,commentId);
    }

    @PatchMapping("/{repostId}/comments/{commentId}")
    public CommentDTO modifyComment(@PathVariable("repostId")Long postId,
                                    @PathVariable("commentId")Long commentId,
                                    @RequestBody CommentModifyRequest request) {
        return repostService.modifyComment(postId, commentId,request.getContent());
    }

    @PostMapping("/{repostId}/comments")
    public CommentDTO addComment(@PathVariable("repostId") Long postId,
                                     @RequestBody CommentWriteRequest request){
        return repostService.addComment(postId, request);
    }

    //like 영역
    @GetMapping("/{repostId}/likes")
    public LikeResponse getLikes(@PathVariable("repostId") Long repostId){
        List<LikeDTO> likes=repostService.getAllLike(repostId);
        return new LikeResponse(likes);
    }

    @DeleteMapping("/{repostId}/likes/{userId}")
    public String deleteLike(@PathVariable("repostId") Long repostId,
                             @PathVariable("userId") Long userId){
        return repostService.deleteLike(repostId, userId);
    }

    @PostMapping("/{repostId}/likes/{userId}")
    public LikeDTO markLike(@PathVariable("repostId") Long repostId,
                           @PathVariable("userId") Long userId){
        return repostService.markLike(repostId,userId);
    }

    @GetMapping("/search")
    public List<RepostDTO> searchByContent(@RequestParam("keyword") String keyword){
        return repostDocService.searchContent(keyword);
    }

}
