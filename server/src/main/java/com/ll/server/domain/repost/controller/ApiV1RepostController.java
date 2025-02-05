package com.ll.server.domain.repost.controller;

import com.ll.server.domain.comment.dto.CommentDTO;
import com.ll.server.domain.comment.dto.CommentModifyRequest;
import com.ll.server.domain.comment.dto.CommentWriteRequest;
import com.ll.server.domain.elasticsearch.repost.service.RepostDocService;
import com.ll.server.domain.like.dto.LikeDTO;
import com.ll.server.domain.like.dto.LikeResponse;
import com.ll.server.domain.repost.dto.RepostDTO;
import com.ll.server.domain.repost.dto.RepostWriteRequest;
import com.ll.server.domain.repost.service.RepostService;
import com.ll.server.global.response.response.ApiResponse;
import com.ll.server.global.response.response.CustomPage;
import com.ll.server.global.utils.MyConstant;
import com.ll.server.global.validation.PageLimitSizeValidator;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reposts")
public class ApiV1RepostController {
    private final RepostService repostService;
    private final RepostDocService repostDocService;


    @Data
    private static class ClientPageRequest{
        private int page=0;
        private int limit=20;
    }

    //repost 영역
    //탭으로 repost 선택 시 호출됨.
    @GetMapping
    public ApiResponse<?> getAllRepost(ClientPageRequest request){
        PageLimitSizeValidator.validateSize(request.getPage(),request.getLimit(), MyConstant.PAGELIMITATION);
        Pageable pageable=PageRequest.of(request.getPage(),request.getLimit(), Sort.by("id").descending());

        Page<RepostDTO> result=repostService.findAll(pageable);

        return ApiResponse.of(CustomPage.of(result));
    }

    @GetMapping("/{repostId}")
    public ApiResponse<RepostDTO> getRepost(@PathVariable("repostId") Long id){
        return ApiResponse.of(repostService.findById(id));
    }

    @DeleteMapping("/{repostId}")
    public ApiResponse<String> deletePost(@PathVariable("repostId") Long id){
        return ApiResponse.of(repostService.delete(id));
    }

    @PostMapping
    public ApiResponse<RepostDTO> write(@RequestBody RepostWriteRequest request){
        return ApiResponse.of(repostService.save(request));
    }

    //comment 영역
    @GetMapping("/{repostId}/comments")
    public ApiResponse<?> getAllComment(@PathVariable("repostId") Long postId,@RequestBody ClientPageRequest request){
        PageLimitSizeValidator.validateSize(request.getPage(),request.getLimit(), MyConstant.PAGELIMITATION);
        Pageable pageable=PageRequest.of(request.getPage(),request.getLimit());
        Page<CommentDTO> result=repostService.getAllComment(postId,pageable);
        return ApiResponse.of(CustomPage.of(result));
    }

    @DeleteMapping("/{repostId}/comments/{commentId}")
    public ApiResponse<String> deleteComment(@PathVariable("repostId")Long postId,
                                @PathVariable("commentId")Long commentId){
        return ApiResponse.of(repostService.deleteComment(postId,commentId));
    }

    @PatchMapping("/{repostId}/comments/{commentId}")
    public ApiResponse<CommentDTO> modifyComment(@PathVariable("repostId")Long postId,
                                    @PathVariable("commentId")Long commentId,
                                    @RequestBody CommentModifyRequest request) {
        return ApiResponse.of(repostService.modifyComment(postId, commentId,request.getContent()));
    }

    @PostMapping("/{repostId}/comments")
    public ApiResponse<CommentDTO> addComment(@PathVariable("repostId") Long postId,
                                     @RequestBody CommentWriteRequest request){
        return ApiResponse.of(repostService.addComment(postId, request));
    }

    //like 영역
    @GetMapping("/{repostId}/likes")
    public ApiResponse<LikeResponse> getLikes(@PathVariable("repostId") Long repostId){
        List<LikeDTO> likes=repostService.getAllLike(repostId);
        return ApiResponse.of(new LikeResponse(likes));
    }

    @DeleteMapping("/{repostId}/likes/{userId}")
    public ApiResponse<String> deleteLike(@PathVariable("repostId") Long repostId,
                             @PathVariable("userId") Long userId){
        return ApiResponse.of(repostService.deleteLike(repostId, userId));
    }

    @PostMapping("/{repostId}/likes/{userId}")
    public ApiResponse<LikeDTO> markLike(@PathVariable("repostId") Long repostId,
                           @PathVariable("userId") Long userId){
        return ApiResponse.of(repostService.markLike(repostId,userId));
    }

    @GetMapping("/search")
    public ApiResponse<?> searchByContent(@RequestParam("keyword") String keyword,
                                           @RequestBody ClientPageRequest request){
        PageLimitSizeValidator.validateSize(request.getPage(),request.getLimit(), MyConstant.PAGELIMITATION);
        Pageable pageable=PageRequest.of(request.getPage(),request.getLimit());
        Page<RepostDTO> result= repostDocService.searchContent(keyword,pageable);
        return ApiResponse.of(CustomPage.of(result));
    }

}
