package com.ll.server.domain.news.news.controller;

import com.ll.server.domain.news.news.dto.NewsDTO;
import com.ll.server.domain.news.news.dto.NewsUpdateRequest;
import com.ll.server.domain.news.news.entity.News;
import com.ll.server.domain.news.news.service.NewsFetchService;
import com.ll.server.domain.news.news.service.NewsService;
import com.ll.server.global.response.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/news")
public class ApiV1NewsController {

    private final NewsService newsService;
    private final NewsFetchService newsFetchService;

    //테스트용 controller
    @GetMapping("/fetchNews")
    public void fetchNews() {
        newsFetchService.fetchNews();
    }

    @GetMapping
    public List<News> getAll() {
        return newsService.getAll();
    }

    //뉴스 조회 API
    @GetMapping("/{id}")
    public ApiResponse<NewsDTO> getById(@PathVariable Long id) {
        News news = newsService.getById(id);
        NewsDTO newsDTO = newsService.convertToDTO(news);

        return ApiResponse.of(newsDTO);
    }

    @PatchMapping("/{id}")
    public ApiResponse<NewsDTO> updateNews(@PathVariable Long id, @RequestBody NewsUpdateRequest request) {
        News news = newsService.updateNews(id, request);
        NewsDTO newsDTO = newsService.convertToDTO(news);

        return ApiResponse.of(newsDTO);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteNews(@PathVariable Long id) {
        newsService.deleteNews(id);

        return ApiResponse.of("삭제 성공");
    }

}