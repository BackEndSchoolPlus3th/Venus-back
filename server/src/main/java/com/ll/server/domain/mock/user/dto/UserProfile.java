package com.ll.server.domain.mock.user.dto;

import com.ll.server.domain.comment.dto.CommentDTO;
import com.ll.server.domain.follow.dto.FolloweeListResponse;
import com.ll.server.domain.follow.dto.FollowerListResponse;
import com.ll.server.domain.mock.user.entity.MockUser;
import com.ll.server.domain.news.news.dto.NewsDTO;
import com.ll.server.domain.repost.dto.RepostDTO;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class UserProfile {
    private MockUserDTO userInfo;
    private List<RepostDTO> reposts;
    private List<RepostDTO> likeReposts;
    private List<CommentDTO> comments;
    private List<NewsDTO> news;
    private FolloweeListResponse followees;
    private FollowerListResponse followers;
    private String profileUrl;

    public UserProfile(MockUser user, FolloweeListResponse followees, FollowerListResponse followers, List<RepostDTO> likeReposts, List<CommentDTO> comments){
        userInfo=new MockUserDTO(user);
        reposts=user.getReposts().stream().map(RepostDTO::new).collect(Collectors.toList());
        this.likeReposts=likeReposts;
        profileUrl=user.getProfileUrl();
        this.followees=followees;
        this.followers=followers;
        this.comments=comments;
    }

    public UserProfile(MockUser user, FolloweeListResponse followees, FollowerListResponse followers, List<RepostDTO> likeReposts, List<CommentDTO> comments, List<NewsDTO> news){
        this(user,followees,followers,likeReposts,comments);
        this.news=news;
    }
}
