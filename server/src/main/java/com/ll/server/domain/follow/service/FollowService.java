package com.ll.server.domain.follow.service;

import com.ll.server.domain.follow.dto.FollowDTO;
import com.ll.server.domain.follow.dto.FolloweeListResponse;
import com.ll.server.domain.follow.dto.FollowerListResponse;
import com.ll.server.domain.follow.entity.Follow;
import com.ll.server.domain.follow.repository.FollowRepository;
import com.ll.server.domain.member.entity.Member;
import com.ll.server.domain.member.repository.MemberRepository;
import com.ll.server.domain.notification.Notify;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FollowService {
    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;

    @Transactional
    @Notify
    public FollowDTO save(Long followerId, Long followeeId){
        if(followerId.equals(followeeId)) return null;

        Member follower=memberRepository.findById(followerId).get();
        Member followee=memberRepository.findById(followeeId).get();

        Follow mockFollow= followRepository.findByFollower_IdAndFollowee_Id(followerId,followeeId);
        if(mockFollow!=null) return null;

        Follow follow= Follow.builder()
                .follower(follower)
                .followee(followee)
                .build();

        return new FollowDTO(followRepository.save(follow));
    }

    public List<FollowDTO> findByFollower(Long followerId){
        return followRepository.findFollowsByFollower_Id(followerId)
                .stream().map(FollowDTO::new)
                .collect(Collectors.toList());
    }

    public List<FollowDTO> findByFollowee(Long followeeId){
        return followRepository.findFollowsByFollowee_Id(followeeId)
                .stream().map(FollowDTO::new)
                .collect(Collectors.toList());
    }

    public FolloweeListResponse findFollowees(String followerName){
        List<Follow> follows= followRepository.findFollowsByFollower_Nickname(followerName);

        return new FolloweeListResponse(follows);
    }


    public FollowerListResponse findFollowers(String followeeName){
        List<Follow> follows= followRepository.findFollowsByFollowee_Nickname(followeeName);

        return new FollowerListResponse(follows);
    }

    public FollowDTO findByFollowerNameAndFolloweeName(String followerName, String followeeName){
        return new FollowDTO(followRepository.findByFollower_NicknameAndFollowee_Nickname(followerName,followeeName));
    }

    public FollowDTO findById(Long followId){
        return new FollowDTO(followRepository.findById(followId).get());
    }

    @Transactional
    public String delete(Long id){
        Optional<Follow> target= followRepository.findById(id);
        if(target.isPresent()){
            followRepository.deleteById(id);
            return "팔로우 삭제 성공";
        }

        return "팔로우 삭제 실패";
    }

}
