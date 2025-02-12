package com.ll.server.domain.news.news.controller;

import com.ll.server.domain.elasticsearch.news.service.NewsDocService;
import com.ll.server.domain.news.news.dto.NewsDTO;
import com.ll.server.domain.news.news.dto.NewsInfinityScrollResponse;
import com.ll.server.domain.news.news.dto.NewsOnly;
import com.ll.server.domain.news.news.dto.NewsUpdateRequest;
import com.ll.server.domain.news.news.entity.News;
import com.ll.server.domain.news.news.service.NewsFetchService;
import com.ll.server.domain.news.news.service.NewsService;
import com.ll.server.domain.repost.service.RepostService;
import com.ll.server.global.response.enums.ReturnCode;
import com.ll.server.global.response.response.ApiResponse;
import com.ll.server.global.response.response.CustomPage;
import com.ll.server.global.utils.MyConstant;
import com.ll.server.global.validation.PageLimitSizeValidator;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/news")
public class ApiV1NewsController {

    private final NewsService newsService;
    private final NewsFetchService newsFetchService;

    private final NewsDocService newsDocService;
    private final RepostService repostService;

    @Data
    private static class NewsGetRequest {
        //requestBody가 들어오지 않을 때 default page, limit 입니다
        private int page = 0;
        private int limit = 20;
    }

    //테스트용 controller
    @GetMapping("/fetchNews")
    public void fetchNews() {
        newsFetchService.fetchNews();
    }

    @GetMapping
    public ApiResponse<?> getAllNews(@RequestParam(value = "keyword", defaultValue = "") String keyword,
                                     @RequestParam(value = "title", defaultValue = "false") boolean hasTitle,
                                     @RequestParam(value = "content", defaultValue = "false") boolean hasContent,
                                     @RequestParam(value = "publisher", defaultValue = "false") boolean hasPublisher,
                                     @RequestParam(value = "category", defaultValue = "") String category,
                                     @RequestParam(value = "page", defaultValue = "0") int page,
                                     @RequestParam(value = "size", defaultValue = "20") int size) {
        //요청한 page, limit이 50을 넘지 않는지 확인
        PageLimitSizeValidator.validateSize(page, size, MyConstant.PAGELIMITATION);
        Pageable pageable = PageRequest.of(page, size);


        //검색을 안 할 때
        if (category.isBlank() && keyword.isBlank()) {
            //Page<NewsDTO> news = newsService.getAll(pageable).map(newsService::convertToDTO);
            Page<NewsDTO> result = newsService.getAll(pageable);
            Page<NewsOnly> news = new PageImpl<>(
                    result.getContent().stream().map(NewsOnly::new).collect(Collectors.toList())
                    , result.getPageable()
                    , result.getTotalElements()
            );
            return ApiResponse.of(CustomPage.of(news));
        }

        if (!keyword.isBlank() && !hasContent && !hasTitle && !hasPublisher) hasTitle = true;

        //검색을 할 때
        Page<NewsOnly> news = newsDocService.search(keyword, hasTitle, hasContent, hasPublisher, category, pageable);
        return ApiResponse.of(CustomPage.of(news));
    }

    @GetMapping("/infinityTest")
    public ApiResponse<?> getAllNewsInfinity(@RequestParam(value = "keyword", defaultValue = "") String keyword,
                                             @RequestParam(value = "title", defaultValue = "false") boolean hasTitle,
                                             @RequestParam(value = "content", defaultValue = "false") boolean hasContent,
                                             @RequestParam(value = "publisher", defaultValue = "false") boolean hasPublisher,
                                             @RequestParam(value = "category", defaultValue = "") String category,
                                             @RequestParam(value = "lastTime", required = false) LocalDateTime lastTime,
                                             @RequestParam(value = "lastId", required = false) Long lastId,
                                             @RequestParam(value = "size", defaultValue = "20") int size) {

        List<NewsOnly> newsList = null;

        //검색이 아니라 그냥 무지성으로 쭉쭉 내릴 때
        if (keyword.isBlank() && category.isBlank()) {
            //최초 전체 조회
            if (lastTime == null) {
                newsList = newsService.firstInfinityGetAll(size);
                return ApiResponse.of(new NewsInfinityScrollResponse(newsList));
            }

            newsList = newsService.afterInfinityGetAll(size, lastTime);
            return ApiResponse.of(new NewsInfinityScrollResponse(newsList));
        }

        //검색을 할 때

        //만약 아무 조건 없이 검색하는 경우
        if (!keyword.isBlank() && !hasContent && !hasTitle && !hasPublisher) hasTitle = true;

        //최초 검색
        if (lastTime == null || lastId == null) {
            //그냥 검색만 하면 제목 기준으로 검색하도록
            newsList = newsDocService.firstInfinitySearch(keyword, hasTitle, hasContent, hasPublisher, category, size);
            return ApiResponse.of(new NewsInfinityScrollResponse(newsList));
        }

        //이후 검색
        newsList = newsDocService.afterInfinitySearch(keyword, hasTitle, hasContent, hasPublisher, category, size, lastTime, lastId);
        return ApiResponse.of(new NewsInfinityScrollResponse(newsList));

    }

    //뉴스 조회 API
    //뉴스 최초 조회 API 전통적인 페이지네이션
    @GetMapping("/{id}")
    public ApiResponse<NewsDTO> getById(@PathVariable("id") Long newsId) {

        News news = newsService.getNews(newsId);
        NewsOnly dto = new NewsOnly(news);

        return ApiResponse.of(dto);
    }

    //뉴스 최초 조회 무한스크롤
    @GetMapping("/infinityTest/{newsId}")
    public ApiResponse<NewsDTO> getByIdInfinity(@PathVariable("newsId") Long newsId) {

        News news = newsService.getNews(newsId);
        NewsOnly newsOnly = new NewsOnly(news);

        return ApiResponse.of(newsOnly);
    }

    @PatchMapping("/{id}")
    public ApiResponse<NewsDTO> updateNews(@PathVariable("id") Long id, @RequestBody NewsUpdateRequest request) {
        NewsDTO newsDTO = newsService.updateNews(id, request);

        return ApiResponse.of(newsDTO);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteNews(@PathVariable Long id) {
        newsService.deleteNews(id);

        return ApiResponse.of(ReturnCode.SUCCESS);
    }

    @GetMapping("/search")
    public ApiResponse<?> searchNews(@RequestParam(value = "keyword", defaultValue = "") String keyword,
                                     @RequestParam(value = "title", defaultValue = "true") boolean hasTitle,
                                     @RequestParam(value = "content", defaultValue = "false") boolean hasContent,
                                     @RequestParam(value = "publisher", defaultValue = "false") boolean hasPublisher,
                                     @RequestParam(value = "category", defaultValue = "") String category,
                                     @RequestBody NewsGetRequest request
    ) {
        //타입으로는 publisher, title, content, category(이건 별도로 드랍다운 방식으로 선택하거나 할 듯. 나머지는 체크박스)가 올 수 있다.
        PageLimitSizeValidator.validateSize(request.getPage(), request.getLimit(), 50);
        Pageable pageable = PageRequest.of(request.getPage(), request.getLimit());
        Page<News> result = newsService.search(keyword, hasTitle, hasContent, hasPublisher, category, pageable);

        return ApiResponse.of(result);
    }
}