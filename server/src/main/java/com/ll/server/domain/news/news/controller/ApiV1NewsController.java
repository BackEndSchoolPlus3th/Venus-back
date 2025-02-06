package com.ll.server.domain.news.news.controller;

import com.ll.server.domain.elasticsearch.news.service.NewsDocService;
import com.ll.server.domain.news.news.dto.NewsDTO;
import com.ll.server.domain.news.news.dto.NewsInfinityScrollResponse;
import com.ll.server.domain.news.news.dto.NewsOnly;
import com.ll.server.domain.news.news.dto.NewsUpdateRequest;
import com.ll.server.domain.news.news.service.NewsFetchService;
import com.ll.server.domain.news.news.service.NewsService;
import com.ll.server.global.response.response.ApiResponse;
import com.ll.server.global.response.response.CustomPage;
import com.ll.server.global.utils.MyConstant;
import com.ll.server.global.validation.PageLimitSizeValidator;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/news")
public class ApiV1NewsController {

    private final NewsService newsService;
    private final NewsFetchService newsFetchService;

    private final NewsDocService newsDocService;

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
    public ApiResponse<?> getAllNews(@RequestParam(value="keyword",defaultValue = "") String keyword,
                                     @RequestParam(value="title",defaultValue="false") boolean hasTitle,
                                     @RequestParam(value="content",defaultValue="false") boolean hasContent,
                                     @RequestParam(value="publisher",defaultValue="false") boolean hasPublisher,
                                     @RequestParam(value="category",defaultValue="") String category,
                                     @RequestBody NewsGetRequest request) {
        //요청한 page, limit이 50을 넘지 않는지 확인
        PageLimitSizeValidator.validateSize(request.getPage(), request.getLimit(), MyConstant.PAGELIMITATION);
        Pageable pageable = PageRequest.of(request.getPage(), request.getLimit(), Sort.by("id").descending());


        //검색을 안 할 때
        if(category.isBlank() && keyword.isBlank()){
            //Page<NewsDTO> news = newsService.getAll(pageable).map(newsService::convertToDTO);
            Page<NewsDTO> result= newsService.getAll(pageable);
            Page<NewsOnly> news=new PageImpl<>(
                    result.getContent().stream().map(NewsOnly::new).collect(Collectors.toList())
                    , result.getPageable()
                    , result.getTotalElements()
            );
            return ApiResponse.of(CustomPage.of(news));
        }

        if(!hasContent || !hasTitle || !hasPublisher || category.isBlank()) hasTitle = true;
        
        //검색을 할 때
        Page<NewsOnly> news=newsDocService.search(keyword,hasTitle,hasContent,hasPublisher,category,pageable);
        return ApiResponse.of(CustomPage.of(news));
    }

    @GetMapping("/infinityTest")
    public ApiResponse<?> searchInfinity(@RequestParam(value="keyword",defaultValue="") String keyword,
                                         @RequestParam(value="title",defaultValue="false") boolean hasTitle,
                                         @RequestParam(value="content",defaultValue="false") boolean hasContent,
                                         @RequestParam(value="publisher",defaultValue="false") boolean hasPublisher,
                                         @RequestParam(value="category",defaultValue = "") String category,
                                         @RequestParam(value="lastId",required = false) Long lastId,
                                         @RequestParam(value="size",defaultValue = "20") int size){

        List<NewsOnly> newsList=null;
        
        //검색이 아니라 그냥 무지성으로 쭉쭉 내릴 때
        if(keyword.isBlank()){
            //최초 검색
            if(lastId==null){
                newsList=newsService.firstInfinityGetAll(size);
                return ApiResponse.of(new NewsInfinityScrollResponse(newsList));
            }

            newsList=newsService.afterInfinityGetAll(size,lastId);
            return ApiResponse.of(new NewsInfinityScrollResponse(newsList));
        }

        //검색을 할 때
        
        //만약 아무 조건 없이 검색하는 경우
        if(!hasContent || !hasTitle || !hasPublisher || category.isBlank()) hasTitle = true;

        //최초 검색
        if(lastId==null) {
            //그냥 검색만 하면 제목 기준으로 검색하도록
            newsList=newsDocService.firstInfinitySearch(keyword, hasTitle, hasContent, hasPublisher, category,size);
            return ApiResponse.of(new NewsInfinityScrollResponse(newsList));
        }

        //이후 검색
        newsList=newsDocService.afterInfinitySearch(keyword, hasTitle, hasContent, hasPublisher, category,size, lastId);
        return ApiResponse.of(new NewsInfinityScrollResponse(newsList));

    }



    //뉴스 조회 API
    @GetMapping("/{id}")
    public ApiResponse<NewsDTO> getById(@PathVariable("id") Long id) {
        NewsDTO newsDTO = newsService.getById(id);
        //NewsDTO newsDTO = newsService.convertToDTO(news);

        return ApiResponse.of(newsDTO);
    }

    @PatchMapping("/{id}")
    public ApiResponse<NewsDTO> updateNews(@PathVariable("id") Long id, @RequestBody NewsUpdateRequest request) {
        NewsDTO newsDTO = newsService.updateNews(id, request);
        //NewsDTO newsDTO = newsService.convertToDTO(news);

        return ApiResponse.of(newsDTO);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteNews(@PathVariable("id") Long id) {
        return ApiResponse.of( newsService.deleteNews(id));
    }

}