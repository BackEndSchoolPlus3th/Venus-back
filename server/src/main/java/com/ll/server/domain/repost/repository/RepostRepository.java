package com.ll.server.domain.repost.repository;

import com.ll.server.domain.repost.entity.Repost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepostRepository extends JpaRepository<Repost, Long> {
    List<Repost> findRepostsByMember_Nickname(String nickname);
}
