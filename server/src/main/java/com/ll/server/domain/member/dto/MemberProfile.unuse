package com.ll.server.domain.member.dto;

import com.ll.server.domain.comment.dto.CommentResponse;
import com.ll.server.domain.follow.dto.FolloweeListResponse;
import com.ll.server.domain.follow.dto.FollowerListResponse;
import com.ll.server.domain.news.news.dto.NewsOnlyResponse;
import com.ll.server.domain.repost.dto.RepostOnlyResponse;
import lombok.Getter;

@Getter
public class MemberProfile {
    private MemberDto userInfo;
    private RepostOnlyResponse reposts;
    private RepostOnlyResponse likeReposts;
    private CommentResponse comments;
    private NewsOnlyResponse news;
    private FollowerListResponse followers;
    private FolloweeListResponse followees;

    public MemberProfile(MemberDto member, RepostOnlyResponse reposts, RepostOnlyResponse likeReposts, CommentResponse comments, FollowerListResponse followers, FolloweeListResponse followees ){
        userInfo=member;
        this.reposts=reposts;
        this.likeReposts=likeReposts;
        this.followees=followees;
        this.followers=followers;
        this.comments=comments;
    }

    public MemberProfile(MemberDto member, RepostOnlyResponse reposts, RepostOnlyResponse likeReposts, CommentResponse comments, FollowerListResponse followers, FolloweeListResponse followees , NewsOnlyResponse news){
        this(member,reposts,likeReposts,comments,followers,followees);
        this.news=news;
    }
}
