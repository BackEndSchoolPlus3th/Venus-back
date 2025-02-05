package com.ll.server.domain.follow.repository;

import com.ll.server.domain.follow.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    List<Follow> findFollowsByFollower_Id(Long followerId);

    List<Follow> findFollowsByFollowee_Id(Long followeeId);

    List<Follow> findFollowsByFollower_Nickname(String nickname);

    List<Follow> findFollowsByFollowee_Nickname(String nickname);

    Follow findByFollower_IdAndFollowee_Id(Long followerId, Long followeeId);

    Follow findByFollower_NicknameAndFollowee_Nickname(String followerName, String followeeName);
}
