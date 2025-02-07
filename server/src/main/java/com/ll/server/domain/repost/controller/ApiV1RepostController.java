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
import com.ll.server.global.jwt.JwtProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reposts")
public class ApiV1RepostController {
    private final RepostService repostService;
    //private final RepostDocService repostDocService;
    private final RepostRepository repostRepository;

    //repost 영역(repost 수정은 없음)

    // 목록 조회
    @GetMapping
    public List<RepostDTO> getAllRepost(){

        return repostService.findAll();
    }

    // 단건 조회
    @GetMapping("/{id}")
    public RepostDTO getRepost(@PathVariable("id") Long id){
        return repostService.findById(id);
    }

    // 삭제
    @DeleteMapping("/{id}")
    public String deletePost(@PathVariable("id") Long repostId, HttpServletRequest request){
        // 토큰 확인
        // 1. 쿠키에서 엑세스 토큰 추출 -> 액세스토큰에서 사용자 정보 추출
        Cookie[] cookies = request.getCookies();
        String accessToken = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("accessToken")) {
                    accessToken = cookie.getValue();  // 쿠키에서 accessToken 값 추출
                }
            }
        }

        // 클래스(JwtProvider)로 바로 접근하지 말고 객체 생성해서 접근!!!!
        JwtProvider jwtProvider = new JwtProvider();
        Map<String, Object> claims = jwtProvider.getClaims(accessToken);
        Object ob_id = claims.get("id"); // 옵젝트로 나옴
        Long userId = Long.valueOf(ob_id.toString()); // string으로 바꿈
        System.out.println("userID = " + userId);
        return repostService.checkDelete_R(repostId, userId);
    };


    // 생성
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public RepostDTO write(@RequestBody RepostWriteRequest request){
        return repostService.save(request);
    }



    //comment 영역
    @GetMapping("/{repostId}/comments")
    public CommentResponse getAllComment(@PathVariable("repostId") Long postId){
        return new CommentResponse(repostService.getAllComment(postId));
    }



    // 삭제
    @DeleteMapping("/{repostId}/comments/{commentId}")
    public String deleteComment(@PathVariable("repostId")Long postId,
                                @PathVariable("commentId")Long id,
                                HttpServletRequest request){
        // 토큰 확인
        // 1. 쿠키에서 엑세스 토큰 추출 -> 액세스토큰에서 사용자 정보 추출
        Cookie[] cookies = request.getCookies();
        String accessToken = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("accessToken")) {
                    accessToken = cookie.getValue();  // 쿠키에서 accessToken 값 추출
                }
            }
        }

        // 같은 유저인지 확인
        JwtProvider jwtProvider = new JwtProvider();
        Map<String, Object> claims = jwtProvider.getClaims(accessToken);
        Object ob_id = claims.get("id"); // 옵젝트로 나옴
        Long userId = Long.valueOf(ob_id.toString()); // string으로 바꿈
        System.out.println("userID = " + userId);
        return repostService.checkDelete_C(postId,id, userId);

    }

    // 수정
    @PatchMapping("/{repostId}/comments/{commentId}")
    public CommentDTO modifyComment(@PathVariable("repostId")Long postId,
                                    @PathVariable("commentId")Long commentId,
                                    @RequestBody CommentModifyRequest request) {
        return repostService.modifyComment(postId, commentId,request.getContent());
    }

    // 작성
    @PreAuthorize("isAuthenticated()")
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

//    @GetMapping("/search")
//    public List<RepostDTO> searchByContent(@RequestParam("keyword") String keyword){
//        return repostDocService.searchContent(keyword);
//    }

}

