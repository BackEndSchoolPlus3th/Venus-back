package com.ll.server.domain.news.news.service;

import com.ll.server.domain.news.news.dto.NewsDTO;
import com.ll.server.domain.news.news.dto.NewsResponse;
import com.ll.server.domain.news.news.entity.News;
import com.ll.server.domain.news.news.enums.NewsCategory;
import com.ll.server.domain.news.news.repository.NewsRepository;
import com.ll.server.domain.notification.Notify;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewsFetchService {
    private final NewsRepository newsRepository;
    private final NewsApiClient newsApiClient;
    private final int DEFAULT_PAGE_SIZE = 100;

    @Notify
    @Transactional
//    @Scheduled(fixedRate = 60 * 60 * 1000) // 1 hour in milliseconds 배포 시 적용
    public NewsResponse fetchNews() {
        LocalDateTime now = LocalDateTime.now();
        String dateFrom = now.format(DateTimeFormatter.ISO_LOCAL_DATE);

        Set<String> existingContentUrls = new HashSet<>(newsRepository.findAllContentUrlByPublishedAtAfter(now.minusDays(1)));

        List<News> newsList = EnumSet.allOf(NewsCategory.class).stream()
                .flatMap(category -> {
                    List<News> articles = newsApiClient.getArticles(category.getCategory(), dateFrom, dateFrom, DEFAULT_PAGE_SIZE).getData()
                            .stream().map(article -> article.toEntity(category))
                            .toList();
                // enum의 category를 순회하며 API 호출
                    return articles.stream();
                })
                .filter(article -> !existingContentUrls.contains(article.getContentUrl()))
                .collect(Collectors.toList());

        return new NewsResponse(
                newsRepository.saveAll(newsList).stream().map(NewsDTO::new).collect(Collectors.toList())
        );
    }
}
