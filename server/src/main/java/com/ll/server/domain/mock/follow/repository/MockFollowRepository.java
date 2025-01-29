package com.ll.server.domain.mock.follow.repository;

import com.ll.server.domain.mock.follow.entity.MockFollow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MockFollowRepository extends JpaRepository<MockFollow, Long> {
    List<MockFollow> findMockFollowsByFollower_Id(Long followerId);

    List<MockFollow> findMockFollowsByFollowee_Id(Long followeeId);

    List<MockFollow> findMockFollowsByFollower_Nickname(String nickname);

    List<MockFollow> findMockFollowsByFollowee_Nickname(String nickname);

    MockFollow findByFollower_IdAndFollowee_Id(Long followerId,Long followeeId);

    MockFollow findByFollower_NicknameAndFollowee_Nickname(String followerName,String followeeName);
}
