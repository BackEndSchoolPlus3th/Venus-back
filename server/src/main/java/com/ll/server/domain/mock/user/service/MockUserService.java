package com.ll.server.domain.mock.user.service;

import com.ll.server.domain.comment.dto.CommentDTO;
import com.ll.server.domain.comment.repository.CommentRepository;
import com.ll.server.domain.follow.dto.FolloweeListResponse;
import com.ll.server.domain.follow.dto.FollowerListResponse;
import com.ll.server.domain.follow.entity.Follow;
import com.ll.server.domain.follow.repository.FollowRepository;
import com.ll.server.domain.like.entity.Like;
import com.ll.server.domain.like.repository.LikeRepository;
import com.ll.server.domain.mock.user.MockRole;
import com.ll.server.domain.mock.user.dto.MockUserLoginRequest;
import com.ll.server.domain.mock.user.dto.MockUserSignupRequest;
import com.ll.server.domain.mock.user.dto.UserProfile;
import com.ll.server.domain.mock.user.entity.MockUser;
import com.ll.server.domain.mock.user.repository.MockUserRepository;
import com.ll.server.domain.news.news.dto.NewsDTO;
import com.ll.server.domain.news.news.repository.NewsRepository;
import com.ll.server.domain.repost.dto.RepostDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MockUserService {
    private final MockUserRepository userRepository;
    private final FollowRepository followRepository;
    private final NewsRepository newsRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public MockUser signup(MockUserSignupRequest request){
        MockUser user=
                MockUser.builder()
                        .email(request.getEmail())
                        .password(request.getPassword())
                        .nickname(request.getNickname())
                        .role(request.getRole())
                        .build();
        MockUser find=userRepository.findByEmail(request.getEmail());

        if(find==null) return userRepository.save(user);

        return null;
    }

    public MockUser login(MockUserLoginRequest request){
        return userRepository.findByEmailAndPassword(request.getEmail(),request.getPassword());
    }

    public MockUser findByNickname(String nickname){
        return userRepository.findByNickname(nickname);
    }

    public List<MockUser> findUsersByNickNameIn(List<String> nicknames){
        return userRepository.findMockUsersByNicknameIn(nicknames);
    }

    public UserProfile getProfile(Long userId){
        Optional<MockUser> targetOptional=userRepository.findById(userId);
        if(targetOptional.isEmpty()) return null;

        MockUser target=targetOptional.get();
        List<Follow> followeeList= followRepository.findMockFollowsByFollower_Id(target.getId());
        List<Follow> followerList= followRepository.findMockFollowsByFollowee_Id(target.getId());

        FolloweeListResponse followees=new FolloweeListResponse(followeeList);
        FollowerListResponse followers=new FollowerListResponse(followerList);

        List<Like> likes=likeRepository.findMockLikesByUser_Id(userId);
        List<RepostDTO> likeReposts=likes.stream().map(like -> new RepostDTO(like.getRepost())).toList();

        List<CommentDTO> comments=commentRepository.findMockCommentsByUser_Id(userId)
                .stream().map(CommentDTO::new).toList();

        if(target.getRole().equals(MockRole.PUBLISHER)){
            List<NewsDTO> news=newsRepository.findNewsByPublisher(target.getNickname())
                    .stream().map(NewsDTO::new).toList();
            return new UserProfile(target,followees,followers,likeReposts,comments,news);
        }

        return new UserProfile(target,followees,followers,likeReposts,comments);


    }
}
