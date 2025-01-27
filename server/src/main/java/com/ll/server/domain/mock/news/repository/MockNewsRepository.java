package com.ll.server.domain.mock.news.repository;

import com.ll.server.domain.mock.news.entity.MockNews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MockNewsRepository extends JpaRepository<MockNews, Long> {
}
