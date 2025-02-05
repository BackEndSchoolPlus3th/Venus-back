package com.ll.server.domain.news.news.service;

import com.ll.server.domain.news.news.dto.NewsDTO;
import com.ll.server.domain.news.news.dto.NewsUpdateRequest;
import com.ll.server.domain.news.news.entity.News;
import com.ll.server.domain.news.news.repository.NewsRepository;
import com.ll.server.domain.notification.Notify;
import com.ll.server.domain.repost.dto.RepostUnderNews;
import com.ll.server.global.response.enums.ReturnCode;
import com.ll.server.global.response.exception.CustomLogicException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NewsService {
    private final NewsRepository newsRepository;

    public Page<NewsDTO> getAll(Pageable pageable) {

        Page<News> result = newsRepository.findAllByOrderByPublishedAtDesc(pageable);
        return new PageImpl<>(
                result.getContent().stream().map(NewsDTO::new).collect(Collectors.toList())
                ,result.getPageable()
                ,result.getTotalElements()
        );
    }

    public NewsDTO getById(Long id) {
        return
                new NewsDTO(
                    newsRepository.findById(id).orElseThrow(() -> new CustomLogicException(ReturnCode.NOT_FOUND_ENTITY))
                );
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
        News news = newsRepository.findById(id).orElseThrow(() -> new CustomLogicException(ReturnCode.NOT_FOUND_ENTITY));
        news.setTitle(request.getTitle());
        news.setContent(request.getContent());

        return new NewsDTO(news);
    }

    @Transactional
    public String deleteNews(Long id) {
        Optional<News> news = newsRepository.findById(id);
        if(news.isEmpty()) return "삭제 실패";

        news.get().removeReposts();

        newsRepository.deleteById(id);
        return "삭제 완료";
    }

    @Transactional
    @Notify
    public NewsDTO saveForTest(News news) {
        News saved=newsRepository.save(news);
        return new NewsDTO(saved);
    }

    public List<NewsDTO> getByPublisher(String publisher){
        return newsRepository.findNewsByPublisher(publisher).stream().map(this::convertToDTO).collect(Collectors.toList());
    }
}
