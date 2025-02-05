package com.ll.server.domain.member.dto;

import com.ll.server.domain.comment.dto.CommentDTO;
import com.ll.server.domain.follow.dto.FolloweeListResponse;
import com.ll.server.domain.follow.dto.FollowerListResponse;
import com.ll.server.domain.member.entity.Member;
import com.ll.server.domain.news.news.dto.NewsDTO;
import com.ll.server.domain.repost.dto.RepostDTO;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class MemberProfile {
    private MemberDto userInfo;
    private List<RepostDTO> reposts;
    private List<RepostDTO> likeReposts;
    private List<CommentDTO> comments;
    private List<NewsDTO> news;
    private FolloweeListResponse followees;
    private FollowerListResponse followers;
    private String profileUrl;

    public MemberProfile(Member member, FolloweeListResponse followees, FollowerListResponse followers, List<RepostDTO> likeReposts, List<CommentDTO> comments){
        userInfo=new MemberDto(member);
        reposts=member.getReposts().stream().map(RepostDTO::new).collect(Collectors.toList());
        this.likeReposts=likeReposts;
        profileUrl=member.getProfile_url();
        this.followees=followees;
        this.followers=followers;
        this.comments=comments;
    }

    public MemberProfile(Member member, FolloweeListResponse followees, FollowerListResponse followers, List<RepostDTO> likeReposts, List<CommentDTO> comments, List<NewsDTO> news){
        this(member,followees,followers,likeReposts,comments);
        this.news=news;
    }
}
