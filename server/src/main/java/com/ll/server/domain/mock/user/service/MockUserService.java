package com.ll.server.domain.mock.user.service;

import com.ll.server.domain.mock.comment.dto.MockCommentDTO;
import com.ll.server.domain.mock.comment.repository.MockCommentRepository;
import com.ll.server.domain.mock.follow.dto.FolloweeListResponse;
import com.ll.server.domain.mock.follow.dto.FollowerListResponse;
import com.ll.server.domain.mock.follow.entity.MockFollow;
import com.ll.server.domain.mock.follow.repository.MockFollowRepository;
import com.ll.server.domain.mock.like.entity.MockLike;
import com.ll.server.domain.mock.like.repository.MockLikeRepository;
import com.ll.server.domain.mock.news.dto.MockNewsDTO;
import com.ll.server.domain.mock.news.repository.MockNewsRepository;
import com.ll.server.domain.mock.repost.dto.MockRepostDTO;
import com.ll.server.domain.mock.user.MockRole;
import com.ll.server.domain.mock.user.dto.MockUserLoginRequest;
import com.ll.server.domain.mock.user.dto.MockUserSignupRequest;
import com.ll.server.domain.mock.user.dto.UserProfile;
import com.ll.server.domain.mock.user.entity.MockUser;
import com.ll.server.domain.mock.user.repository.MockUserRepository;
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
    private final MockFollowRepository mockFollowRepository;
    private final MockNewsRepository newsRepository;
    private final MockLikeRepository likeRepository;
    private final MockCommentRepository mockCommentRepository;

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
        List<MockFollow> followeeList=mockFollowRepository.findMockFollowsByFollower_Id(target.getId());
        List<MockFollow> followerList=mockFollowRepository.findMockFollowsByFollowee_Id(target.getId());

        FolloweeListResponse followees=new FolloweeListResponse(followeeList);
        FollowerListResponse followers=new FollowerListResponse(followerList);

        List<MockLike> likes=likeRepository.findMockLikesByUser_Id(userId);
        List<MockRepostDTO> likeReposts=likes.stream().map(like -> new MockRepostDTO(like.getRepost())).toList();

        List<MockCommentDTO> comments=mockCommentRepository.findMockCommentsByUser_Id(userId)
                .stream().map(MockCommentDTO::new).toList();

        if(target.getRole().equals(MockRole.PUBLISHER)){
            List<MockNewsDTO> news=newsRepository.findMockNewsByPublisher(target.getNickname())
                    .stream().map(MockNewsDTO::new).toList();
            return new UserProfile(target,followees,followers,likeReposts,comments,news);
        }

        return new UserProfile(target,followees,followers,likeReposts,comments);


    }
}
