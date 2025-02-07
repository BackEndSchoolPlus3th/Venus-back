package com.ll.server.domain.news.news.controller;

import com.ll.server.domain.elasticsearch.news.service.NewsDocService;
import com.ll.server.domain.news.news.dto.*;
import com.ll.server.domain.news.news.entity.News;
import com.ll.server.domain.news.news.service.NewsFetchService;
import com.ll.server.domain.news.news.service.NewsService;
import com.ll.server.domain.repost.dto.RepostInfinityResponse;
import com.ll.server.domain.repost.dto.RepostUnderNews;
import com.ll.server.domain.repost.service.RepostService;
import com.ll.server.global.response.enums.ReturnCode;
import com.ll.server.global.response.response.ApiResponse;
import com.ll.server.global.response.response.CustomPage;
import com.ll.server.global.utils.MyConstant;
import com.ll.server.global.validation.PageLimitSizeValidator;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
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
                                         @RequestParam(value="lastTime",required = false) LocalDateTime lastTime,
                                         @RequestParam(value ="lastId",required = false) Long lastId,
                                         @RequestParam(value="size",defaultValue = "20") int size){

        List<NewsOnly> newsList=null;

        //검색이 아니라 그냥 무지성으로 쭉쭉 내릴 때
        if(keyword.isBlank()){
            //최초 검색
            if(lastTime==null){
                newsList=newsService.firstInfinityGetAll(size);
                return ApiResponse.of(new NewsInfinityScrollResponse(newsList));
            }

            newsList=newsService.afterInfinityGetAll(size,lastTime);
            return ApiResponse.of(new NewsInfinityScrollResponse(newsList));
        }

        //검색을 할 때

        //만약 아무 조건 없이 검색하는 경우
        if(!hasContent || !hasTitle || !hasPublisher || category.isBlank()) hasTitle = true;

        //최초 검색
        if(lastTime==null) {
            //그냥 검색만 하면 제목 기준으로 검색하도록
            newsList=newsDocService.firstInfinitySearch(keyword, hasTitle, hasContent, hasPublisher, category,size);
            return ApiResponse.of(new NewsInfinityScrollResponse(newsList));
        }

        //이후 검색
        newsList=newsDocService.afterInfinitySearch(keyword, hasTitle, hasContent, hasPublisher, category,size, lastTime,lastId);
        return ApiResponse.of(new NewsInfinityScrollResponse(newsList));

    }



    //뉴스 조회 API
    //뉴스 최초 조회 API
    @GetMapping("/{id}")
    public ApiResponse<NewsPageDetail> getById(@PathVariable("id") Long id,
                                               @RequestParam(value = "size",defaultValue = "20") int size) {
        int page=0;
        PageLimitSizeValidator.validateSize(page, size, MyConstant.PAGELIMITATION);
        Pageable pageable = PageRequest.of(page, size);

        News news = newsService.getNews(id);
        CustomPage<RepostUnderNews> repostPage = CustomPage.of(repostService.getNewsRepost(id,pageable));
        NewsPageDetail newsPageDetail = new NewsPageDetail(news,repostPage);

        return ApiResponse.of(newsPageDetail);
    }

    //뉴스의 리포스트 무한 스크롤 요청 시 (전통적인 페이지네이션으로 반환하는) 엔드포인트
    @GetMapping("/{id}/reposts")
    public ApiResponse<?> getUnderReposts(@PathVariable("id") Long id,
                                          @RequestParam(value = "page",defaultValue = "1") int page,
                                          @RequestParam(value = "size",defaultValue = "20") int size){
        PageLimitSizeValidator.validateSize(page, size, MyConstant.PAGELIMITATION);
        Pageable pageable = PageRequest.of(page, size);

        CustomPage<RepostUnderNews> repostPage=CustomPage.of(repostService.getNewsRepost(id,pageable));

        return ApiResponse.of(repostPage);
    }


    //뉴스 최초 조회 무한스크롤
    @GetMapping("/infinityTest/{id}")
    public ApiResponse<NewsDTO> getByIdInfinity(@PathVariable("id") Long id,
                                                @RequestParam(value = "size", defaultValue = "20") int size){

        News news = newsService.getNews(id);
        RepostInfinityResponse reposts = new RepostInfinityResponse(repostService.firstGetNewsRepost(id,size));

        return ApiResponse.of(new NewsInfinityDetail(news,reposts));
    }

    @GetMapping("/infinityTest/{id}/reposts")
    public ApiResponse<?> getUnderRepostsInfinity(@PathVariable("id") Long id,
                                                @RequestParam(value = "size", defaultValue = "20") int size,
                                                  @RequestParam(value = "lastTime")LocalDateTime lastTime,
                                                  @RequestParam(value = "lastId")Long lastRepostId){

        List<RepostUnderNews> reposts=repostService.firstGetNewsRepost(id,size);
        RepostInfinityResponse response=new RepostInfinityResponse(reposts);
        return ApiResponse.of(response);
    }

    @PatchMapping("/{id}")
    public ApiResponse<NewsDTO> updateNews(@PathVariable("id") Long id, @RequestBody NewsUpdateRequest request) {
        NewsDTO newsDTO = newsService.updateNews(id, request);

        return ApiResponse.of(newsDTO);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteNews(@PathVariable Long id) {
        News news = newsService.getNews(id);
        news.removeReposts();
        news.delete();

        return ApiResponse.of(ReturnCode.SUCCESS);
    }

}