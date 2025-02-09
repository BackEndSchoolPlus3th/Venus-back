package com.ll.server.domain.like.repository;

import com.ll.server.domain.like.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like,Long> {
    List<Like> findLikesByMember_IdAndDeletedIsFalse(Long userId);
}
