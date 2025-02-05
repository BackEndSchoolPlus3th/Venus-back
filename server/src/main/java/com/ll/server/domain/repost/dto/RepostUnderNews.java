package com.ll.server.domain.repost.dto;

import com.ll.server.domain.comment.dto.CommentDTO;
import com.ll.server.domain.comment.dto.CommentResponse;
import com.ll.server.domain.like.dto.LikeDTO;
import com.ll.server.domain.like.dto.LikeResponse;
import com.ll.server.domain.mention.repostmention.dto.RepostMentionDTO;
import com.ll.server.domain.repost.entity.Repost;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Builder
@Getter
public class RepostUnderNews {
    private Long repostId;
    private Long writerId;
    private String nickname;
    private String content;
    private List<RepostMentionDTO> mentions;
    private CommentResponse commentInfo;
    private LikeResponse likeInfo;
    private String imageUrl;

    public RepostUnderNews(Repost repost){
        repostId=repost.getId();
        writerId=repost.getMember().getId();
        nickname=repost.getMember().getNickname();
        content=repost.getContent();
        mentions=repost.getMentions().stream()
                .map(RepostMentionDTO::new)
                .collect(Collectors.toList());

        commentInfo=new CommentResponse(
                repost.getComments()
                        .stream().filter(comment -> comment.getDeletedAt()==null)
                        .map(CommentDTO::new)
                        .collect(Collectors.toList())
        );

        likeInfo=new LikeResponse(
                repost.getLikes()
                        .stream().filter(comment -> !comment.getDeleted())
                        .map(LikeDTO::new)
                        .collect(Collectors.toList())
        );

        imageUrl = repost.getImageUrl();

    }
}
