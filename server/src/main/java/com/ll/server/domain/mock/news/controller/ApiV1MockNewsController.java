package com.ll.server.domain.mock.news.controller;

import com.ll.server.domain.mock.news.dto.MockNewsDTO;
import com.ll.server.domain.mock.news.dto.NewsUpdateRequest;
import com.ll.server.domain.mock.news.service.MockNewsService;
import com.ll.server.global.response.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mock/news")
public class ApiV1MockNewsController {

    private final MockNewsService mockNewsService;

    @GetMapping
    public List<MockNewsDTO> getAll() {
        return mockNewsService.getAll();
    }

    //뉴스 조회 API
    @GetMapping("/{id}")
    public ApiResponse<MockNewsDTO> getById(@PathVariable Long id) {
        MockNewsDTO mockNewsDTO = mockNewsService.getById(id);

        return ApiResponse.of(mockNewsDTO);
    }

    @PatchMapping("/{id}")
    public ApiResponse<MockNewsDTO> updateNews(@PathVariable Long id, @RequestBody NewsUpdateRequest request) {
        MockNewsDTO mockNewsDTO = mockNewsService.updateNews(id, request);

        return ApiResponse.of(mockNewsDTO);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteNews(@PathVariable Long id) {
        mockNewsService.deleteNews(id);

        return ApiResponse.of("삭제 성공");
    }

}