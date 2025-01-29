package com.ll.server.domain.mock.news.service;

import com.ll.server.domain.mock.news.dto.MockNewsDTO;
import com.ll.server.domain.mock.news.dto.MockNewsResponse;
import com.ll.server.domain.mock.news.dto.NewsUpdateRequest;
import com.ll.server.domain.mock.news.entity.MockNews;
import com.ll.server.domain.mock.news.repository.MockNewsRepository;
import com.ll.server.domain.news.news.dto.NewsArticleParam;
import com.ll.server.domain.news.news.service.NewsApiClient;
import com.ll.server.domain.notification.Notify;
import com.ll.server.global.response.enums.ReturnCode;
import com.ll.server.global.response.exception.CustomLogicException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MockNewsService {
    private final MockNewsRepository mockNewsRepository;
    private final NewsApiClient newsApiClient;
    private final int DEFAULT_PAGE_SIZE = 100;

    public List<MockNewsDTO> getAll() {
        return mockNewsRepository.findAll()
                .stream().map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Notify
    public MockNewsDTO saveForTest(MockNews news){
        return convertToDTO(mockNewsRepository.save(news));
    }

    @Notify
    public MockNewsResponse fetchNews() {
        LocalDateTime now = LocalDateTime.now();
        String dateFrom = now.minusHours(6).format(DateTimeFormatter.ISO_LOCAL_DATE);
        String dateTo = now.format(DateTimeFormatter.ISO_LOCAL_DATE);

        List<NewsArticleParam> politicArticles = newsApiClient.getArticles("politics", dateFrom, dateTo, DEFAULT_PAGE_SIZE).getData();

        List<MockNews> mockNewsList = convertToNews(politicArticles);

        return new MockNewsResponse(
                mockNewsRepository.saveAll(mockNewsList)
                .stream().map(this::convertToDTO)
                .collect(Collectors.toList())
        );

    }

    private List<MockNews> convertToNews(List<NewsArticleParam> fetchParams) {
        return fetchParams.stream()
                .map(param -> MockNews.builder()
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

      public MockNewsDTO getById(Long id) {
        return convertToDTO(mockNewsRepository.findById(id).orElseThrow(() -> new CustomLogicException(ReturnCode.NOT_FOUND_ENTITY)));
    }

    public List<MockNewsDTO> getByPublisher(String publisher){
        return mockNewsRepository.findMockNewsByPublisher(publisher).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Convert News Entity to DTO
    public MockNewsDTO convertToDTO(MockNews mockNews) {
        return new MockNewsDTO(
                mockNews.getId(),
                mockNews.getTitle(),
                mockNews.getContent(),
                mockNews.getAuthor(),
                mockNews.getPublisher(), // Assuming Publisher has a getter for its name
                mockNews.getImageUrl(),
                mockNews.getThumbnailUrl(),
                mockNews.getContentUrl()
        );
    }

    @Transactional
    public MockNewsDTO updateNews(Long id, NewsUpdateRequest request) {
        MockNews mockNews = mockNewsRepository.findById(id).orElseThrow(() -> new CustomLogicException(ReturnCode.NOT_FOUND_ENTITY));
        mockNews.setTitle(request.getTitle());
        mockNews.setContent(request.getContent());

        return convertToDTO(mockNews);
    }

    @Transactional
    public void deleteNews(Long id) {
        mockNewsRepository.deleteById(id);
    }
}
