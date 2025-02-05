package com.ll.server.domain.follow.repository;

import com.ll.server.domain.follow.entity.Follow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    Page<Follow> findFollowsByFollower_Id(Long followerId, Pageable pageable);
    List<Follow> findFollowsByFollower_Id(Long followerId);

    Page<Follow> findFollowsByFollowee_Id(Long followeeId, Pageable pageable);

    Page<Follow> findFollowsByFollower_Nickname(String nickname, Pageable pageable);
    List<Follow> findFollowsByFollower_Nickname(String nickname);

    Page<Follow> findFollowsByFollowee_Nickname(String nickname, Pageable pageable);

    Follow findByFollower_IdAndFollowee_Id(Long followerId, Long followeeId);

    Follow findByFollower_NicknameAndFollowee_Nickname(String followerName, String followeeName);
}
