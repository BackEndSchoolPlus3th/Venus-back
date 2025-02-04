package com.ll.server.domain.repost.dto;

import com.ll.server.domain.comment.dto.CommentDTO;
import com.ll.server.domain.comment.dto.CommentResponse;
import com.ll.server.domain.like.dto.LikeDTO;
import com.ll.server.domain.like.dto.LikeResponse;
import com.ll.server.domain.mention.repostmention.dto.RepostMentionDTO;
import com.ll.server.domain.news.news.dto.NewsDTO;
import com.ll.server.domain.news.news.entity.News;
import com.ll.server.domain.repost.entity.Repost;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Builder
@Getter
public class RepostDTO {
    private Long repostId;
    private Long writerId;
    private String nickname;
    private NewsDTO news;
    private String content;
    private List<RepostMentionDTO> mentions;
    private CommentResponse commentInfo;
    private LikeResponse likeInfo;
    private String imageUrl;

    public RepostDTO(Repost repost){
        News newsEntity=repost.getNews();
        news= NewsDTO.builder()
                .publisherName(newsEntity.getPublisher())
                .author(newsEntity.getAuthor())
                .id(newsEntity.getId())
                .title(newsEntity.getTitle())
                .content(newsEntity.getContent())
                .contentUrl(newsEntity.getContentUrl())
                .imageUrl(newsEntity.getImageUrl())
                .thumbnailUrl(newsEntity.getThumbnailUrl())
                .build();
        repostId=repost.getId();
        writerId=repost.getUser().getId();
        nickname=repost.getUser().getNickname();
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
