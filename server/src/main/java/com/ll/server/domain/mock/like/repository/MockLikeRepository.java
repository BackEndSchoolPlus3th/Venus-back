package com.ll.server.domain.mock.like.repository;

import com.ll.server.domain.mock.like.entity.MockLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MockLikeRepository extends JpaRepository<MockLike,Long> {
    List<MockLike> findMockLikesByUser_Id(Long userId);
}
