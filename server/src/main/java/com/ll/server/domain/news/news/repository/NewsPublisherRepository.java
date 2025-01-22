package com.ll.server.domain.news.news.repository;

import com.ll.server.domain.news.news.entity.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsPublisherRepository extends JpaRepository<Publisher, Long> {
}
