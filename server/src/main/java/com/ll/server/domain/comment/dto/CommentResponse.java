package com.ll.server.domain.comment.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class CommentResponse {
    private List<CommentDTO> comments;
    private long count;

    public CommentResponse(List<CommentDTO> comments) {
        this.comments = comments;
        count = comments.size();
    }
}
