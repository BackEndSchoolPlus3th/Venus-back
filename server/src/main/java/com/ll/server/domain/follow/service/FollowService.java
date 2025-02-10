package com.ll.server.domain.follow.service;

import com.ll.server.domain.follow.dto.FollowDTO;
import com.ll.server.domain.follow.entity.Follow;
import com.ll.server.domain.follow.repository.FollowRepository;
import com.ll.server.domain.member.entity.Member;
import com.ll.server.domain.member.repository.MemberRepository;
import com.ll.server.domain.notification.Notify;
import com.ll.server.global.response.enums.ReturnCode;
import com.ll.server.global.response.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        Follow find= followRepository.findByFollower_IdAndFollowee_Id(followerId,followeeId);
        if(find!=null) return null;

        Follow follow= Follow.builder()
                .follower(follower)
                .followee(followee)
                .build();

        return new FollowDTO(followRepository.save(follow));
    }

    public Page<FollowDTO> findByFollower(Long followerId, Pageable pageable){
        Page<Follow> result= followRepository.findFollowsByFollower_Id(followerId,pageable);
        return new PageImpl<>(
                result.getContent().stream().map(FollowDTO::new).collect(Collectors.toList()),
                result.getPageable(),
                result.getTotalElements()
        );
    }

    public Page<FollowDTO> findByFollowee(Long followeeId,Pageable pageable){
        Page<Follow> result= followRepository.findFollowsByFollowee_Id(followeeId,pageable);
        return new PageImpl<>(
                result.getContent().stream().map(FollowDTO::new).collect(Collectors.toList()),
                result.getPageable(),
                result.getTotalElements()
        );
    }

    public Page<FollowDTO>  findFollowees(String followerName,Pageable pageable){
        Page<Follow> result= followRepository.findFollowsByFollower_Nickname(followerName,pageable);
        return new PageImpl<>(
          result.getContent().stream().map(FollowDTO::new).collect(Collectors.toList()),
                result.getPageable(),
                result.getTotalElements()
        );
    }


    public Page<FollowDTO>  findFollowers(String followeeName, Pageable pageable){
        Page<Follow> result= followRepository.findFollowsByFollowee_Nickname(followeeName,pageable);
        return new PageImpl<>(
                result.getContent().stream().map(FollowDTO::new).collect(Collectors.toList()),
                result.getPageable(),
                result.getTotalElements()
        );
    }

    public FollowDTO findByFollowerNameAndFolloweeName(String followerName, String followeeName){
        return new FollowDTO(followRepository.findByFollower_NicknameAndFollowee_Nickname(followerName,followeeName));
    }

    public FollowDTO findById(Long followId){
        return new FollowDTO(followRepository.findById(followId).get());
    }

    @Transactional
    public void delete(Long id){
        Follow target= followRepository.findById(id)
                .orElseThrow(()->new CustomException(ReturnCode.NOT_FOUND_ENTITY));

        followRepository.deleteById(id);
    }

}
