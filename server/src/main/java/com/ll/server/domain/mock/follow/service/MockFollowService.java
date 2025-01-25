package com.ll.server.domain.mock.follow.service;

import com.ll.server.domain.mock.follow.dto.FolloweeListResponse;
import com.ll.server.domain.mock.follow.dto.FollowerListResponse;
import com.ll.server.domain.mock.follow.dto.MockFollowDTO;
import com.ll.server.domain.mock.follow.entity.MockFollow;
import com.ll.server.domain.mock.follow.repository.MockFollowRepository;
import com.ll.server.domain.mock.user.entity.MockUser;
import com.ll.server.domain.mock.user.repository.MockUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MockFollowService {
    private final MockFollowRepository mockFollowRepository;
    private final MockUserRepository userRepository;

    @Transactional
    public MockFollowDTO save(Long followerId, Long followeeId){
        if(followerId.equals(followeeId)) return null;

        MockUser follower=userRepository.findById(followerId).get();
        MockUser followee=userRepository.findById(followeeId).get();

        MockFollow mockFollow= mockFollowRepository.findByFollower_IdAndFollowee_Id(followerId,followeeId);
        if(mockFollow!=null) return null;

        MockFollow follow= MockFollow.builder()
                .follower(follower)
                .followee(followee)
                .build();

        return new MockFollowDTO(mockFollowRepository.save(follow));
    }

    public List<MockFollowDTO> findByFollower(Long followerId){
        return mockFollowRepository.findMockFollowsByFollower_Id(followerId)
                .stream().map(MockFollowDTO::new)
                .collect(Collectors.toList());
    }

    public List<MockFollowDTO> findByFollowee(Long followeeId){
        return mockFollowRepository.findMockFollowsByFollowee_Id(followeeId)
                .stream().map(MockFollowDTO::new)
                .collect(Collectors.toList());
    }

    public FolloweeListResponse findFollowees(String followerName){
        List<MockFollow> follows=mockFollowRepository.findMockFollowsByFollower_Nickname(followerName);
        List<String> followees=new ArrayList<>();

        for(MockFollow follow: follows){
            followees.add(follow.getFollowee().getNickname());
        }

        return new FolloweeListResponse(followees);
    }


    public FollowerListResponse findFollowers(String followeeName){
        List<MockFollow> follows=mockFollowRepository.findMockFollowsByFollowee_Nickname(followeeName);
        List<String> followees=new ArrayList<>();

        for(MockFollow follow: follows){
            followees.add(follow.getFollower().getNickname());
        }

        return new FollowerListResponse(followees);
    }

    public MockFollowDTO findByFollowerNameAndFolloweeName(String followerName,String followeeName){
        return new MockFollowDTO(mockFollowRepository.findByFollower_NicknameAndFollowee_Nickname(followerName,followeeName));
    }

    public MockFollowDTO findById(Long followId){
        return new MockFollowDTO(mockFollowRepository.findById(followId).get());
    }

    @Transactional
    public String delete(Long id){
        Optional<MockFollow> target=mockFollowRepository.findById(id);
        if(target.isPresent()){
            mockFollowRepository.deleteById(id);
            return "팔로우 삭제 성공";
        }

        return "팔로우 삭제 실패";
    }

}
