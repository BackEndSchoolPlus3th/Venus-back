package com.ll.server.domain.repost.repository;

import com.ll.server.domain.member.entity.Member;
import com.ll.server.domain.repost.entity.Repost;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RepostRepository extends JpaRepository<Repost, Long> {

    //멤버의 닉네임을 기반으로 repost 검색
    List<Repost> findRepostsByMember_NicknameAndDeletedAtIsNull(String nickname);

    //멤버의 ID를 기반으로 repost 검색
    Page<Repost> findRepostsByMemberAndDeletedAtIsNull(Member member, Pageable pageable);

    Page<Repost> findAllByDeletedAtIsNull(Pageable pageable);

    @Query("""
            SELECT r FROM Repost r\s
            WHERE r.news.id = :newsId AND r.news.deletedAt IS NULL AND r.deletedAt IS NULL\s
            ORDER BY\s
            r.pinned DESC,\s
            r.createDate DESC,\s
            r.id DESC\s
            """)
    List<Repost> firstGetNewsReposts(@Param("newsId") Long newsId,
                                     Limit limit);

    @Query("""
            SELECT r FROM Repost r\s
            WHERE r.news.id = :newsId AND r.createDate < :lastTime AND r.id < :lastId AND r.pinned = false AND r.news.deletedAt IS NULL AND r.deletedAt IS NULL\s
            ORDER BY\s
            r.createDate DESC,\s
            r.id DESC
            """)
    //첫 페이지에만 고정된 것들이 있다고 가정한다.
    List<Repost> afterGetNewsReposts(@Param("newsId") Long newsId,
                                     @Param("lastTime") LocalDateTime lastTime,
                                     @Param("lastId") Long lastId,
                                     Limit limit);

    @Query("""
            SELECT r FROM Repost r\s
            WHERE r.news.id = :newsId AND r.news.deletedAt IS NULL AND r.deletedAt IS NULL\s
            ORDER BY\s
            r.pinned DESC ,\s
            r.createDate DESC,\s
            r.id DESC
            """)
    Page<Repost> getNewsReposts(@Param("newsId") Long newsId, Pageable pageable);


    List<Repost> findAllByDeletedAtIsNullOrderByCreateDateDescIdDesc(Limit limit);

    List<Repost> findAllByDeletedAtIsNullAndCreateDateBeforeAndIdLessThanOrderByCreateDateDescIdDesc(LocalDateTime lastTime,Long lastId, Limit limit);

    List<Repost> findAllByIdInAndDeletedAtIsNullOrderByCreateDateDescIdDesc(List<Long> ids);

    Repost findRepostByNewsIdAndPinnedIsTrueAndDeletedAtIsNull(Long newsId);

    List<Repost> findByContentContainingAndDeletedAtIsNull(String keyword);

    @Query("SELECT r FROM Repost r INNER JOIN r.likes l WHERE r.createDate >= :startOfDay GROUP BY r.id ORDER BY COUNT(l) DESC")
    List<Repost> findTodayshotReposts(@Param("startOfDay") LocalDateTime startOfDay, Pageable pageable);
}
