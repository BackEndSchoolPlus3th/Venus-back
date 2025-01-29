package com.ll.server.domain.news.news.service;

import com.ll.server.domain.news.news.dto.NewsDTO;
import com.ll.server.domain.news.news.dto.NewsUpdateRequest;
import com.ll.server.domain.news.news.entity.News;
import com.ll.server.domain.news.news.repository.NewsRepository;
import com.ll.server.global.response.enums.ReturnCode;
import com.ll.server.global.response.exception.CustomLogicException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NewsService {
    private final NewsRepository newsRepository;

    public List<News> getAll() {
        return newsRepository.findAll();
    }

    public News getById(Long id) {
        return newsRepository.findById(id).orElseThrow(() -> new CustomLogicException(ReturnCode.NOT_FOUND_ENTITY));
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
                news.getContentUrl()
        );
    }

    @Transactional
    public News updateNews(Long id, NewsUpdateRequest request) {
        News news = newsRepository.findById(id).orElseThrow(() -> new CustomLogicException(ReturnCode.NOT_FOUND_ENTITY));
        news.setTitle(request.getTitle());
        news.setContent(request.getContent());

        return news;
    }

    @Transactional
    public void deleteNews(Long id) {
        newsRepository.deleteById(id);
    }
}
