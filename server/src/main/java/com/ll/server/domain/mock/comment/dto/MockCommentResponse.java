package com.ll.server.domain.mock.comment.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class MockCommentResponse {
    private List<MockCommentDTO> comments;
    private long count;

    public MockCommentResponse(List<MockCommentDTO> comments){
        this.comments=comments;
        count=comments.size();
    }
}
