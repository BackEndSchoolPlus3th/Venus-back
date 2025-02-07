package com.ll.server.domain.repost.repository;

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
    List<Repost> findRepostsByMember_Nickname(String nickname);
    List<Repost> findRepostsByMember_Id(Long memberId);

    @Query("""
            SELECT '*' FROM Repost r\s
            WHERE r.news.id = :newsId
            ORDER BY\s
            CASE WHEN r.pinned = true THEN 0 ELSE 1 END,\s
            r.createDate DESC,\s
            r.id DESC\s
            """)
    List<Repost> firstGetNewsReposts(@Param("newsId") Long newsId,
                                     Limit limit);

    @Query("""
            SELECT '*' FROM Repost r\s
            WHERE r.news.id = :newsId
            ORDER BY\s
            CASE WHEN r.pinned = true THEN 0 ELSE 1 END,\s
            r.createDate DESC,\s
            r.id DESC
            """)
    List<Repost> afterGetNewsReposts(@Param("newsId") Long newsId,
                                     @Param("lastTime") LocalDateTime lastTime,
                                     @Param("lastId") Long lastId,
                                     Limit limit);

    @Query("""
            SELECT '*' FROM Repost r\s
            WHERE r.news.id = :newsId
            ORDER BY\s
            CASE WHEN r.pinned = true THEN 0 ELSE 1 END,\s
            r.createDate DESC,\s
            r.id DESC
            """)
    Page<Repost> getNewsReposts(@Param("newsId") Long newsId, Pageable pageable);
}
