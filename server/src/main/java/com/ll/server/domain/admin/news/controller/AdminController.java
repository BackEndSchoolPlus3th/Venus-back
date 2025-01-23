package com.ll.server.domain.admin.news.controller;

import com.ll.server.domain.news.news.dto.NewsDTO;
import com.ll.server.domain.news.news.dto.NewsUpdateRequest;
import com.ll.server.domain.news.news.entity.News;
import com.ll.server.domain.news.news.service.NewsService;
import com.ll.server.global.response.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final NewsService newsService;

    @GetMapping("/news")
    public List<News> newsGetAll() {
        return newsService.getAll();
    }

    @GetMapping("/news/{id}")
    public ApiResponse<NewsDTO> newsGetById(@PathVariable Long id) {
        News news = newsService.getById(id);
        NewsDTO newsDTO = newsService.convertToDTO(news);

        return ApiResponse.of(newsDTO);
    }

    @PatchMapping("/news/{id}")
    public ApiResponse<NewsDTO> newsUpdate(@PathVariable Long id, @RequestBody NewsUpdateRequest request) {
        News news = newsService.updateNews(id, request);
        NewsDTO newsDTO = newsService.convertToDTO(news);

        return ApiResponse.of(newsDTO);
    }

    @DeleteMapping("/news/{id}")
    public ApiResponse<String> newsDelete(@PathVariable Long id) {
        newsService.deleteNews(id);

        return ApiResponse.of("삭제 성공 - 관리자");
    }
}
