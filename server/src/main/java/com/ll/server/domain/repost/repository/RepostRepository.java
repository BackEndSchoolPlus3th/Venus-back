package com.ll.server.domain.repost.repository;

import com.ll.server.domain.repost.entity.Repost;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RepostRepository extends JpaRepository<Repost, Long> {
    List<Repost> findRepostsByMember_Nickname(String nickname);
    List<Repost> findRepostsByMember_Id(Long memberId);

    Page<Repost> findAllByOrderByCreateDateDescIdDesc(Pageable pageable);

    List<Repost> findAllByOrderByCreateDateDescIdDesc(Limit limit);

    List<Repost> findAllByCreateDateBeforeAndIdLessThanOrderByCreateDateDescIdDesc(LocalDateTime lastTime,Long lastId, Limit limit);

    List<Repost> findAllByIdInOrderByCreateDateDescIdDesc(List<Long> ids);
}
