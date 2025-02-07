package com.ll.server.domain.repost.dto;

import com.ll.server.domain.comment.dto.CommentDTO;
import com.ll.server.global.response.response.CustomPage;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RepostPageDetail {
    private Long repostId;
    private Long writerId;
    private String nickname;
    private String content;
    private CustomPage<CommentDTO> comments;
    private int likeCount;
    private String imageUrl;
    private LocalDateTime createDate;
    private String memberProfileImageUrl;

    public RepostPageDetail(RepostDTO repost, CustomPage<CommentDTO> comments){
        repostId=repost.getRepostId();
        writerId=repost.getWriterId();
        nickname=repost.getNickname();
        content=repost.getContent();
        this.comments=comments;
        likeCount= (int)repost.getLikeInfo().getCount();
        imageUrl=repost.getImageUrl();
        createDate=repost.getCreateDate();
        memberProfileImageUrl=repost.getMemberProfileImageUrl();
    }
}
