package com.ll.server.domain.news.news.repository;

import com.ll.server.domain.news.news.entity.News;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    List<News> findNewsByPublisher(String publisher);

    Page<News> findAllByOrderByPublishedAtDesc(Pageable pageable);

    List<News> findAllByIdIn(List<Long> ids, Sort sort);

    List<News> findAllByOrderByIdDesc(Limit limit);

    List<News> findAllByIdLessThan(Long lastId,Limit limit);
}
