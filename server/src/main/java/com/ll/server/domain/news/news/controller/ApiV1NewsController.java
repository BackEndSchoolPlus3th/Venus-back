package com.ll.server.domain.news.news.controller;

import com.ll.server.domain.news.news.dto.NewsDTO;
import com.ll.server.domain.news.news.dto.NewsUpdateRequest;
import com.ll.server.domain.news.news.entity.News;
import com.ll.server.domain.news.news.service.NewsFetchService;
import com.ll.server.domain.news.news.service.NewsService;
import com.ll.server.global.response.enums.ReturnCode;
import com.ll.server.global.response.response.ApiResponse;
import com.ll.server.global.response.response.CustomPage;
import com.ll.server.global.utils.MyConstant;
import com.ll.server.global.validation.PageLimitSizeValidator;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/news")
public class ApiV1NewsController {

    private final NewsService newsService;
    private final NewsFetchService newsFetchService;

    //private final NewsDocService newsDocService;

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
    public ApiResponse<?> getAllNews(NewsGetRequest request) {
        //요청한 page, limit이 50을 넘지 않는지 확인
        PageLimitSizeValidator.validateSize(request.getPage(), request.getLimit(), MyConstant.PAGELIMITATION);
        Pageable pageable = PageRequest.of(request.getPage(), request.getLimit(), Sort.by("id").descending());

        Page<NewsDTO> news = newsService.getAll(pageable);

        return ApiResponse.of(CustomPage.of(news));
    }

    //뉴스 조회 API
    @GetMapping("/{id}")
    public ApiResponse<NewsDTO> getById(@PathVariable("id") Long id) {
//        NewsDTO newsDTO = newsService.getById(id);
        News news = newsService.getNews(id);
        NewsDTO newsDTO = new NewsDTO(news);

        return ApiResponse.of(newsDTO);
    }

    @PatchMapping("/{id}")
    public ApiResponse<NewsDTO> updateNews(@PathVariable("id") Long id, @RequestBody NewsUpdateRequest request) {
        NewsDTO newsDTO = newsService.updateNews(id, request);
        //NewsDTO newsDTO = newsService.convertToDTO(news);

        return ApiResponse.of(newsDTO);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteNews(@PathVariable Long id) {
        News news = newsService.getNews(id);
        news.removeReposts();
        news.delete();

        return ApiResponse.of(ReturnCode.SUCCESS);
    }

//    @GetMapping("/search")
//    public ApiResponse<?> searchNews(@RequestParam(value="keyword",defaultValue="") String keyword,
//                                           @RequestParam(value="title",defaultValue=true) boolean hasTitle,
//                                           @RequestParam(value="content",defaultValue=false) boolean hasContent,
//                                           @RequestParam(value="publisher",defaultValue=false) boolean hasPublisher,
//                                           @RequestParam(value="category",defaultValue="") String category,
//                                           @RequestBody NewsGetRequest request
//                                           ){
//        //타입으로는 publisher, title, content, category(이건 별도로 드랍다운 방식으로 선택하거나 할 듯. 나머지는 체크박스)가 올 수 있다.
//        PageLimitSizeValidator.validateSize(request.getPage(), request.getLimit(), 50);
//        Pageable pageable = PageRequest.of(request.getPage(), request.getLimit());
//        Page<News> result = newsDocService.search(keyword, hasTitle, hasContent, hasPublisher, category, request);
//    }

}