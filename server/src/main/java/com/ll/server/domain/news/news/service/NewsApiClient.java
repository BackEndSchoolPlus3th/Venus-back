package com.ll.server.domain.news.news.service;

import com.ll.server.domain.news.news.dto.NewsApiResponseParam;
import com.ll.server.domain.news.news.dto.NewsArticleParam;
import com.ll.server.global.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "newsApiClient", url = "https://api-v2.deepsearch.com", configuration = FeignConfig.class)
public interface NewsApiClient {

    @GetMapping("/v1/articles/{category}")
    NewsApiResponseParam<NewsArticleParam> getArticles(
            @PathVariable("category") String category,
            @RequestParam("date_from") String dateFrom,
            @RequestParam("date_to") String dateTo,
            @RequestParam("page_size") int pageSize
    );
}
