package com.ll.server.domain.news.news.service;

import com.ll.server.domain.news.news.controller.ApiV1NewsController;
import com.ll.server.domain.news.news.dto.NewsDTO;
import com.ll.server.domain.news.news.dto.NewsUpdateRequest;
import com.ll.server.domain.news.news.entity.News;
import com.ll.server.domain.news.news.repository.NewsRepository;
import com.ll.server.domain.notification.Notify;
import com.ll.server.global.response.enums.ReturnCode;
import com.ll.server.global.response.exception.CustomRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NewsService {
    private final NewsRepository newsRepository;

    public Page<NewsDTO> getAll(Pageable pageable) {

        Page<News> result = newsRepository.findAllByOrderByPublishedAtDesc(pageable);
        return new PageImpl<>(
                result.getContent().stream()
                        .filter(news -> news.getDeletedAt() == null)
                        .map(NewsDTO::new)
                        .collect(Collectors.toList())
                , result.getPageable()
                , result.getTotalElements()
        );
    }

    @Transactional
    public NewsDTO updateNews(Long id, NewsUpdateRequest request) {
        News news = getNews(id);

        news.setContent(request.getContent());
        news.setTitle(request.getTitle());
        return new NewsDTO(news);
    }

    @Transactional
    @Notify
    public NewsDTO saveForTest(News news) {
        News saved = newsRepository.save(news);
        return new NewsDTO(saved);
    }

    public List<NewsDTO> getByPublisher(String publisher) {
        return newsRepository.findNewsByPublisher(publisher).stream()
                .filter(news -> news.getDeletedAt() == null)
                .map(NewsDTO::new).collect(Collectors.toList());
    }

    public News getNews(Long newsId) {
        return newsRepository.findById(newsId).orElseThrow(() -> new CustomRequestException(ReturnCode.NOT_FOUND_ENTITY));
    }

    public Page<News> search(String keyword, boolean hasTitle, boolean hasContent, boolean hasPublisher, String category, Pageable pageable) {
        Specification<News> spec = Specification.where(null);

        if (hasTitle) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("title"), "%" + keyword + "%"));
        }
        if (hasContent) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("content"), "%" + keyword + "%"));
        }
        if (hasPublisher) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("publisher"), "%" + keyword + "%"));
        }
        if (!category.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("category").get("category"), category));
        }

        return newsRepository.findAll(spec, pageable);
    }
}
