package com.ll.server.domain.mock.comment.repository;

import com.ll.server.domain.mock.comment.entity.MockComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MockCommentRepository extends JpaRepository<MockComment,Long> {

    List<MockComment> findMockCommentsByUser_Id(Long userId);
}
