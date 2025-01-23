package com.ll.server.domain.news.news.service;

import com.ll.server.domain.news.news.dto.NewsFetchParam;
import com.ll.server.domain.news.news.entity.News;
import com.ll.server.domain.news.news.repository.NewsRepository;
import com.ll.server.global.response.enums.ReturnCode;
import com.ll.server.global.response.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class NewsService {
    private final NewsRepository newsRepository;
    private final NewsApiClient newsApiClient;
    private final int DEFAULT_PAGE_SIZE = 100;

    public List<News> getAll() {
        return newsRepository.findAll();
    }

    public ApiResponse<?> fetchNews() {
        LocalDateTime now = LocalDateTime.now();
        String dateFrom = now.minusHours(6).format(DateTimeFormatter.ISO_LOCAL_DATE);
        String dateTo = now.format(DateTimeFormatter.ISO_LOCAL_DATE);

        List<NewsFetchParam> politicArticles = newsApiClient.getArticles("politics", dateFrom, dateTo, DEFAULT_PAGE_SIZE).getData();
        List<NewsFetchParam> globalPoliticArticles = newsApiClient.getGlobalArticles("politics", dateFrom, dateTo, DEFAULT_PAGE_SIZE).getData()
                .stream()
                .map(global -> NewsFetchParam.builder()
                        .title(global.getTitleKo())
                        .publisher(global.getPublisher())
                        .author(global.getAuthor())
                        .summary(global.getSummaryKo())
                        .imageUrl(global.getImageUrl())
                        .thumbnailUrl(global.getThumbnailUrl())
                        .contentUrl(global.getContentUrl())
                        .publishedAt(global.getPublishedAt())
                        .build())
                .toList();


        List<News> newsList = Stream.concat(
                convertToNews(politicArticles).stream(),
                convertToNews(globalPoliticArticles).stream()
        ).collect(Collectors.toList());

        newsRepository.saveAll(newsList);

        return ApiResponse.of(ReturnCode.SUCCESS);
    }

    private List<News> convertToNews(List<NewsFetchParam> fetchParams) {
        return fetchParams.stream()
                .map(param -> News.builder()
                        .title(param.getTitle())
                        .content(param.getSummary())
                        .author(param.getAuthor())
                        .imageUrl(param.getImageUrl())
                        .thumbnailUrl(param.getThumbnailUrl())
                        .contentUrl(param.getContentUrl())
                        .createDate(LocalDateTime.parse(param.getPublishedAt().replace("Z", "")))
                        .build()
                ).collect(Collectors.toList());
    }
}
