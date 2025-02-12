package com.ll.server.domain.news.news.service;

import com.ll.server.domain.elasticsearch.news.service.NewsDocService;
import com.ll.server.domain.elasticsearch.repost.service.RepostDocService;
import com.ll.server.domain.news.news.dto.NewsDTO;
import com.ll.server.domain.news.news.dto.NewsOnly;
import com.ll.server.domain.news.news.dto.NewsUpdateRequest;
import com.ll.server.domain.news.news.entity.News;
import com.ll.server.domain.news.news.repository.NewsRepository;
import com.ll.server.domain.notification.Notify;
import com.ll.server.domain.repost.dto.RepostUnderNews;
import com.ll.server.global.response.enums.ReturnCode;
import com.ll.server.global.response.exception.CustomException;
import com.ll.server.global.response.exception.CustomRequestException;
import com.ll.server.global.security.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NewsService {
    private final NewsRepository newsRepository;
    private final NewsDocService newsDocService;
    private final RepostDocService repostDocService;

    public Page<NewsDTO> getAll(Pageable pageable) {
        Page<News> result = newsRepository.findAllByDeletedAtIsNullOrderByPublishedAtDescIdDesc(pageable);

        return new PageImpl<>(
                result.getContent().stream()
                        .map(NewsDTO::new)
                        .collect(Collectors.toList())
                , result.getPageable()
                , result.getTotalElements()
        );
    }

    public List<NewsOnly> firstInfinityGetAll(int size) {
        List<News> result = newsRepository.findAllByDeletedAtIsNullOrderByPublishedAtDescIdDesc(Limit.of(size));

        return result.stream()
                .map(NewsOnly::new)
                .collect(Collectors.toList());
    }

    public List<NewsOnly> afterInfinityGetAll(int size, LocalDateTime lastTime) {
        List<News> result = newsRepository.findAllByPublishedAtIsBeforeAndDeletedAtIsNullOrderByPublishedAtDescIdDesc(lastTime, Limit.of(size));

        return result.stream()
                .map(NewsOnly::new)
                .collect(Collectors.toList());
    }


    public NewsDTO getById(Long id) {
        News news = getNews(id);

        return new NewsDTO(news);
    }

    // Convert News Entity to DTO
    public NewsDTO convertToDTO(News news) {
        return new NewsDTO(
                news.getId(),
                news.getTitle(),
                news.getContent(),
                news.getAuthor(),
                news.getPublisher(), // Assuming Publisher has a getter for its name
                news.getImageUrl(),
                news.getThumbnailUrl(),
                news.getContentUrl(),
                news.getCategory().getCategory(),
                news.getPublishedAt(),
                news.getReposts().stream().map(RepostUnderNews::new).collect(Collectors.toList())
        );
    }

    @Transactional
    public NewsDTO updateNews(Long id, NewsUpdateRequest request) {
        News news = getNews(id);

        checkAdmin();

        news.setContent(request.getContent());
        news.setTitle(request.getTitle());
        return new NewsDTO(news);
    }


    @Transactional
    public void deleteNews(Long id) {
        News news = getNews(id);

        checkAdmin();

        news.removeReposts();
        news.setDeletedAt(LocalDateTime.now());

    }

    @Transactional
    @Notify
    public NewsDTO saveForTest(News news) {
        News saved = newsRepository.save(news);
        return new NewsDTO(saved);
    }

    public List<NewsDTO> getByPublisher(String publisher) {
        return newsRepository.findNewsByPublisherAndDeletedAtIsNull(publisher).stream()
                .map(NewsDTO::new).collect(Collectors.toList());
    }

    public News getNews(Long newsId) {
        return newsRepository.findById(newsId).orElseThrow(() -> new CustomRequestException(ReturnCode.NOT_FOUND_ENTITY));
    }

    private void checkAdmin() {
        Collection<? extends GrantedAuthority> authorizations = AuthUtil.getAuth();

        if (authorizations == null || !authorizations.contains("ADMIN")) {
            throw new CustomException(ReturnCode.NOT_AUTHORIZED);
        }
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
