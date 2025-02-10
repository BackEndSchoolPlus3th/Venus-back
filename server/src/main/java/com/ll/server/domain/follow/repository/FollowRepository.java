package com.ll.server.domain.follow.repository;

import com.ll.server.domain.follow.entity.Follow;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    Page<Follow> findFollowsByFollower_Id(Long followerId, Pageable pageable);
    List<Follow> findFollowsByFollower_Id(Long followerId);

    Page<Follow> findFollowsByFollowee_Id(Long followeeId, Pageable pageable);
    List<Follow> findFollowsByFollowee_Id(Long followeeId);

    Page<Follow> findFollowsByFollower_Nickname(String nickname, Pageable pageable);

    List<Follow> findFollowsByFollower_Nickname(String nickname);

    //구독자들 찾기
    long countFollowsByFollower_Nickname(String Nickname);

    //페이지네이션 최초. 팔로잉하는 친구들 찾기
    List<Follow> findFollowsByFollower_Nickname(String nickname, Limit limit);


    //페이지네이션 2번째 이후. 팔로잉하는 친구들 찾기
    List<Follow> findFollowsByFollower_NicknameAndIdGreaterThan(String nickname,Long lastId, Limit limit);
    //구독자들 찾기 끝


    //구독한 사람 찾기 영역
    long countFollowsByFollowee_Nickname(String Nickname);

    //페이지네이션 최초. 팔로우한 친구들 찾기
    List<Follow> findFollowsByFollowee_Nickname(String nickname, Limit limit);

    //페이지네이션 2번째 이후. 팔로우한 친구들 찾기
    List<Follow> findFollowsByFollowee_NicknameAndIdGreaterThan(String nickname,Long lastId, Limit limit);
    //구독한 사람 찾기 끝

    Page<Follow> findFollowsByFollowee_Nickname(String nickname, Pageable pageable);


    Follow findByFollower_IdAndFollowee_Id(Long followerId, Long followeeId);

    Follow findByFollower_NicknameAndFollowee_Nickname(String followerName, String followeeName);
}
