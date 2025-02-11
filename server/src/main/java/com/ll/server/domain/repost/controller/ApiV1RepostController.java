package com.ll.server.domain.repost.controller;

import com.ll.server.domain.comment.dto.CommentDTO;
import com.ll.server.domain.comment.dto.CommentModifyRequest;
import com.ll.server.domain.comment.dto.CommentWriteRequest;
import com.ll.server.domain.like.dto.LikeDTO;
import com.ll.server.domain.like.dto.LikeResponse;
import com.ll.server.domain.repost.dto.RepostDTO;
import com.ll.server.domain.repost.dto.RepostWriteRequest;
import com.ll.server.domain.repost.entity.Repost;
import com.ll.server.domain.repost.service.RepostService;
import com.ll.server.global.response.enums.ReturnCode;
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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reposts")
public class ApiV1RepostController {
    private final RepostService repostService;
    //private final RepostDocService repostDocService;


    @Data
    private static class ClientPageRequest {
        private int page = 0;
        private int limit = 20;
    }

    //repost 영역
    //탭으로 repost 선택 시 호출됨.
    @GetMapping
    public ApiResponse<?> getAllRepost(ClientPageRequest request) {
        PageLimitSizeValidator.validateSize(request.getPage(), request.getLimit(), MyConstant.PAGELIMITATION);
        Pageable pageable = PageRequest.of(request.getPage(), request.getLimit(), Sort.by("id").descending());

        Page<RepostDTO> result = repostService.findAll(pageable);

        return ApiResponse.of(CustomPage.of(result));
    }

    @GetMapping("/{repostId}")
    public ApiResponse<RepostDTO> getRepost(@PathVariable("repostId") Long id) {
        Repost repost = repostService.getRepost(id);
        RepostDTO repostDTO = new RepostDTO(repost);
        return ApiResponse.of(repostDTO);
    }

    @DeleteMapping("/{repostId}")
    public ApiResponse<String> deletePost(@PathVariable("repostId") Long id) {
        Repost repost = repostService.getRepost(id);
        repost.deleteComments();
        repost.deleteLikes();
        repost.delete();

        return ApiResponse.of(ReturnCode.SUCCESS);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RepostDTO write(
            @RequestPart("request") RepostWriteRequest request, // JSON 데이터
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile // 이미지 파일 (선택 사항)
    ) throws IOException {
        return repostService.save(request, imageFile);
    }

    //comment 영역
    @GetMapping("/{repostId}/comments")
    public ApiResponse<?> getAllComment(@PathVariable("repostId") Long postId, @RequestBody ClientPageRequest request) {
        PageLimitSizeValidator.validateSize(request.getPage(), request.getLimit(), MyConstant.PAGELIMITATION);
        Pageable pageable = PageRequest.of(request.getPage(), request.getLimit());
        Page<CommentDTO> result = repostService.getAllComment(postId, pageable);
        return ApiResponse.of(CustomPage.of(result));
    }

    @DeleteMapping("/{repostId}/comments/{commentId}")
    public ApiResponse<String> deleteComment(@PathVariable("repostId") Long postId,
                                             @PathVariable("commentId") Long commentId) {
        repostService.deleteComment(postId, commentId);
        return ApiResponse.of(ReturnCode.SUCCESS);
    }

    @PatchMapping("/{repostId}/comments/{commentId}")
    public ApiResponse<CommentDTO> modifyComment(@PathVariable("repostId") Long postId,
                                                 @PathVariable("commentId") Long commentId,
                                                 @RequestBody CommentModifyRequest request) {
        return ApiResponse.of(repostService.modifyComment(postId, commentId, request.getContent()));
    }

    @PostMapping("/{repostId}/comments")
    public ApiResponse<CommentDTO> addComment(@PathVariable("repostId") Long postId,
                                              @RequestBody CommentWriteRequest request) {
        return ApiResponse.of(repostService.addComment(postId, request));
    }

    //like 영역
    @GetMapping("/{repostId}/likes")
    public ApiResponse<LikeResponse> getLikes(@PathVariable("repostId") Long repostId) {
        List<LikeDTO> likes = repostService.getAllLike(repostId);
        return ApiResponse.of(new LikeResponse(likes));
    }

    @DeleteMapping("/{repostId}/likes/{userId}")
    public ApiResponse<String> deleteLike(@PathVariable("repostId") Long repostId,
                                          @PathVariable("userId") Long userId) {

        repostService.deleteLike(repostId, userId);
        return ApiResponse.of(ReturnCode.SUCCESS);
    }

    @PostMapping("/{repostId}/likes/{userId}")
    public ApiResponse<LikeDTO> markLike(@PathVariable("repostId") Long repostId,
                                         @PathVariable("userId") Long userId) {
        return ApiResponse.of(repostService.markLike(repostId, userId));
    }

//    @GetMapping("/search")
//    public List<RepostDTO> searchByContent(@RequestParam("keyword") String keyword){
//        return repostDocService.searchContent(keyword);
//    }

        @GetMapping("/search")
    public List<RepostDTO> searchByContent(@RequestParam("keyword") String keyword){
        return repostService.searchContent(keyword);
    }

}
