package com.ll.server.domain.repost.dto;

import com.ll.server.domain.comment.dto.CommentInfinityScrollResponse;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RepostInfinityDetail {
    private Long repostId;
    private Long writerId;
    private String nickname;
    private String content;
    private CommentInfinityScrollResponse comments;
    private int likeCount;
    private String imageUrl;
    private LocalDateTime createDate;
    private String memberProfileImageUrl;

    public RepostInfinityDetail(RepostDTO repost, CommentInfinityScrollResponse comments){
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
