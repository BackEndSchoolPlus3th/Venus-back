package com.ll.server.domain.news.news.repository;

import com.ll.server.domain.news.news.entity.News;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

    //퍼블리셔 이름으로 삭제 안 된 기사를 찾음
    List<News> findNewsByPublisherAndDeletedAtIsNull(String publisher);

    //삭제 안된 기사를 페이지 단위로 찾는다. 정렬 기준은 최신 기사순, 최신 아이디순
    Page<News> findAllByDeletedAtIsNullOrderByPublishedAtDescIdDesc(Pageable pageable);

    //삭제 안된 기사를 몇 개 가져온다. 정렬 기준은 최신 기사순, 낮은 아이디순. 왜냐면 기사 출판일과 아이디는 관련이 없기 때문
    List<News> findAllByDeletedAtIsNullOrderByPublishedAtDescIdDesc(Limit limit);

    //삭제 안된 기사를 lastTime 기준으로 몇 개 가져온다. 정렬 기준은 최신 기사순, 낮은 아이디순. 왜냐면 기사 출판일과 아이디는 관련이 없기 때문
    List<News> findAllByPublishedAtIsBeforeAndDeletedAtIsNullOrderByPublishedAtDescIdDesc(LocalDateTime lastTime, Limit limit);
}
