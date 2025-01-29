package com.ll.server.domain.mock.repost.repository;

import com.ll.server.domain.mock.repost.entity.MockRepost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MockRepostRepository extends JpaRepository<MockRepost, Long> {
    List<MockRepost> findMockRepostsByUser_Nickname(String nickname);
}
