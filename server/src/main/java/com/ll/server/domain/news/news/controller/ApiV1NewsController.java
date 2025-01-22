package com.ll.server.domain.news.news.controller;

import com.ll.server.domain.news.news.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/news")
public class ApiV1NewsController {

    private final NewsService newsService;

    @GetMapping("/getAll")
    public String getAll() {
        return newsService.getAll().toString();
    }
}
