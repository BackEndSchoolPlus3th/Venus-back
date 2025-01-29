package com.ll.server.domain.news.news.service;

import com.ll.server.domain.news.news.entity.News;
import com.ll.server.domain.news.news.enums.NewsCategory;
import com.ll.server.domain.news.news.repository.NewsRepository;
import com.ll.server.global.response.enums.ReturnCode;
import com.ll.server.global.response.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewsFetchService {
    private final NewsRepository newsRepository;
    private final NewsApiClient newsApiClient;
    private final int DEFAULT_PAGE_SIZE = 100;

    public ApiResponse<?> fetchNews() {
        LocalDateTime now = LocalDateTime.now();
        String dateFrom = now.minusHours(6).format(DateTimeFormatter.ISO_LOCAL_DATE);
        String dateTo = now.format(DateTimeFormatter.ISO_LOCAL_DATE);

        List<News> newsList = EnumSet.allOf(NewsCategory.class).stream()
                .flatMap(category -> {
                    List<News> articles = newsApiClient.getArticles(category.getCategory(), dateFrom, dateTo, DEFAULT_PAGE_SIZE).getData()
                            .stream().map(article -> article.toEntity(category))
                            .toList();
                // enum의 category를 순회하며 API 호출
                    return articles.stream();
                })
                .collect(Collectors.toList());

        newsRepository.saveAll(newsList);

        return ApiResponse.of(ReturnCode.SUCCESS);
    }
}
