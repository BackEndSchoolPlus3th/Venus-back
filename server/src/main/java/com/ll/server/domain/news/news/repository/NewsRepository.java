package com.ll.server.domain.news.news.repository;

import com.ll.server.domain.news.news.entity.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long>, JpaSpecificationExecutor<News> {
    List<News> findNewsByPublisher(String publisher);

    Page<News> findAllByOrderByPublishedAtDesc(Pageable pageable);
}
