package com.ll.server.domain.mock.user.dto;

import com.ll.server.domain.mock.comment.dto.MockCommentDTO;
import com.ll.server.domain.mock.follow.dto.FolloweeListResponse;
import com.ll.server.domain.mock.follow.dto.FollowerListResponse;
import com.ll.server.domain.mock.news.dto.MockNewsDTO;
import com.ll.server.domain.mock.repost.dto.MockRepostDTO;
import com.ll.server.domain.mock.user.entity.MockUser;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class UserProfile {
    private MockUserDTO userInfo;
    private List<MockRepostDTO> reposts;
    private List<MockRepostDTO> likeReposts;
    private List<MockCommentDTO> comments;
    private List<MockNewsDTO> news;
    private FolloweeListResponse followees;
    private FollowerListResponse followers;
    private String profileUrl;

    public UserProfile(MockUser user, FolloweeListResponse followees, FollowerListResponse followers, List<MockRepostDTO> likeReposts, List<MockCommentDTO> comments){
        userInfo=new MockUserDTO(user);
        reposts=user.getReposts().stream().map(MockRepostDTO::new).collect(Collectors.toList());
        this.likeReposts=likeReposts;
        profileUrl=user.getProfileUrl();
        this.followees=followees;
        this.followers=followers;
        this.comments=comments;
    }

    public UserProfile(MockUser user,FolloweeListResponse followees, FollowerListResponse followers, List<MockRepostDTO> likeReposts, List<MockCommentDTO> comments,List<MockNewsDTO> news){
        this(user,followees,followers,likeReposts,comments);
        this.news=news;
    }
}
