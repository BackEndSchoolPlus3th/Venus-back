package com.ll.server.domain.repost.controller;

import com.ll.server.domain.comment.dto.CommentDTO;
import com.ll.server.domain.comment.dto.CommentModifyRequest;
import com.ll.server.domain.comment.dto.CommentResponse;
import com.ll.server.domain.comment.dto.CommentWriteRequest;
import com.ll.server.domain.like.dto.LikeDTO;
import com.ll.server.domain.like.dto.LikeResponse;
import com.ll.server.domain.repost.dto.RepostDTO;
import com.ll.server.domain.repost.dto.RepostWriteRequest;
import com.ll.server.domain.repost.repository.RepostRepository;
import com.ll.server.domain.repost.service.RepostService;
import com.ll.server.global.jwt.AccessTokenFromCookie;
import com.ll.server.global.jwt.JwtProvider;
import com.ll.server.global.security.SecurityUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reposts")
public class ApiV1RepostController {
    private final RepostService repostService;
    //private final RepostDocService repostDocService;
    private final RepostRepository repostRepository;
    private final AccessTokenFromCookie accessTokenFromCookie;
    private final JwtProvider jwtProvider;


    /*
        repost 영역
    */

    // 목록 조회
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public List<RepostDTO> getAllRepost(){
        return repostService.findAll();
    }

    // 단건 조회
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public RepostDTO getRepost(@PathVariable("id") Long id){
        return repostService.findById(id);
    }

    // 삭제
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public String deletePost(@PathVariable("id") Long repostId) {
        // auth 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof SecurityUser) {
            SecurityUser user = (SecurityUser) authentication.getPrincipal();
            Long userId = user.getId(); // email 가져오기

            return repostService.checkDelete_R(repostId, userId);
        } else {
            throw new RuntimeException("인증 실패");
        }
    }

    // 생성
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public RepostDTO write(@RequestBody RepostWriteRequest request){
        return repostService.save(request);
    }



    /*
        comment 영역
    */

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{repostId}/comments")
    public CommentResponse getAllComment(@PathVariable("repostId") Long postId){
        return new CommentResponse(repostService.getAllComment(postId));
    }



    // 삭제
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{repostId}/comments/{commentId}")
    public String deleteComment(@PathVariable("repostId")Long postId,
                                @PathVariable("commentId")Long commentId
                                ){
        // auth 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof SecurityUser) {
            SecurityUser user = (SecurityUser) authentication.getPrincipal();
            Long userId = user.getId(); // email 가져오기

            return repostService.checkDelete_C(postId,commentId, userId);
        } else {
            throw new RuntimeException("인증 실패");
        }
    }


    // 수정
    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{repostId}/comments/{commentId}")
    public CommentDTO modifyComment(@PathVariable("repostId") Long postId,
                                    @PathVariable("commentId") Long commentId,
                                    @RequestBody CommentModifyRequest requestBody
                                    ) {
        // auth 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof SecurityUser) {
            SecurityUser user = (SecurityUser) authentication.getPrincipal();
            Long userId = user.getId(); //
            return repostService.modify_C(postId, commentId, requestBody.getContent(), userId);

        } else {
            throw new RuntimeException("인증 실패");
        }
    }


    // 작성

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{repostId}/comments")
    public CommentDTO addComment(@PathVariable("repostId") Long postId,
                                     @RequestBody CommentWriteRequest request){
        return repostService.addComment(postId, request);
    }

    /*
        like 영역
    */


    @GetMapping("/{repostId}/likes")
    public LikeResponse getLikes(@PathVariable("repostId") Long repostId){
        List<LikeDTO> likes=repostService.getAllLike(repostId);
        return new LikeResponse(likes);
    }
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{repostId}/likes/{userId}")
    public String deleteLike(@PathVariable("repostId") Long repostId,
                             @PathVariable("userId") Long userId){
        return repostService.deleteLike(repostId, userId);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{repostId}/likes/{userId}")
    public LikeDTO markLike(@PathVariable("repostId") Long repostId,
                           @PathVariable("userId") Long userId){
        return repostService.markLike(repostId,userId);
    }

//    @GetMapping("/search")
//    public List<RepostDTO> searchByContent(@RequestParam("keyword") String keyword){
//        return repostDocService.searchContent(keyword);
//    }

}

