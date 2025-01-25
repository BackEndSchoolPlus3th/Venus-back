package com.ll.server.domain.mock.repost.dto;

import com.ll.server.domain.mention.repostmention.dto.RepostMentionDTO;
import com.ll.server.domain.mock.comment.dto.MockCommentDTO;
import com.ll.server.domain.mock.comment.dto.MockCommentResponse;
import com.ll.server.domain.mock.like.dto.MockLikeDTO;
import com.ll.server.domain.mock.like.dto.MockLikeResponse;
import com.ll.server.domain.mock.repost.entity.MockRepost;
import com.ll.server.domain.news.news.dto.NewsDTO;
import com.ll.server.domain.news.news.entity.News;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Builder
@Getter
public class MockRepostDTO {
    private Long repostId;
    private Long writerId;
    private String nickname;
    private NewsDTO news;
    private String content;
    private List<RepostMentionDTO> mentions;
    private MockCommentResponse commentInfo;
    private MockLikeResponse likeInfo;

    public MockRepostDTO(MockRepost repost){
        News newsEntity=repost.getNews();
        news= NewsDTO.builder()
                .publisherName(newsEntity.getPublisher().getPublisher())
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

        commentInfo=new MockCommentResponse(
                repost.getComments()
                .stream().filter(comment -> comment.getDeletedAt()==null)
                .map(MockCommentDTO::new)
                .collect(Collectors.toList())
        );

        likeInfo=new MockLikeResponse(
                repost.getLikes()
                        .stream().filter(comment -> !comment.getDeleted())
                        .map(MockLikeDTO::new)
                        .collect(Collectors.toList())
        );



    }
}
