package com.ll.server.domain.news.news.service;

import com.ll.server.domain.news.news.dto.NewsDTO;
import com.ll.server.domain.news.news.dto.NewsOnly;
import com.ll.server.domain.news.news.dto.NewsUpdateRequest;
import com.ll.server.domain.news.news.entity.News;
import com.ll.server.domain.news.news.repository.NewsRepository;
import com.ll.server.domain.notification.Notify;
import com.ll.server.domain.repost.dto.RepostUnderNews;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
                result.getContent().stream()
                        .filter(news->news.getDeletedAt()==null)
                        .map(NewsDTO::new)
                        .collect(Collectors.toList())
                ,result.getPageable()
                ,result.getTotalElements()
        );
    }

    public List<NewsOnly> firstInfinityGetAll(int size){
        List<News> result=newsRepository.findAllByOrderByIdDesc(Limit.of(size));
        return result.stream().filter(news->news.getDeletedAt()==null)
                .map(NewsOnly::new)
                .collect(Collectors.toList());
    }

    public List<NewsOnly> afterInfinityGetAll(int size, long lastId){
        List<News> result=newsRepository.findAllByIdLessThan(lastId,Limit.of(size));
        return result.stream().filter(news->news.getDeletedAt()==null)
                .map(NewsOnly::new)
                .collect(Collectors.toList());
    }


    public NewsDTO getById(Long id) {
        News news=getNews(id);
        if(news==null){
            return null;
        }

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
        News news=getNews(id);
        if(news==null){
            return null;
        }

        news.setContent(request.getContent());
        news.setTitle(request.getTitle());
        return new NewsDTO(news);
    }

    @Transactional
    public String deleteNews(Long id) {
        News news=getNews(id);
        if(news==null){
            return "삭제 실패";
        }

        news.removeReposts();
        news.setDeletedAt(LocalDateTime.now());

        return "삭제 성공";

    }

    @Transactional
    @Notify
    public NewsDTO saveForTest(News news) {
        News saved=newsRepository.save(news);
        return new NewsDTO(saved);
    }

    public List<NewsDTO> getByPublisher(String publisher){
        return newsRepository.findNewsByPublisher(publisher).stream()
                .filter(news->news.getDeletedAt()==null)
                .map(this::convertToDTO).collect(Collectors.toList());
    }

    private News getNews(Long newsId) {
        Optional<News> newsOptional=newsRepository.findById(newsId);
        if(newsOptional.isEmpty()) return null;

        News news=newsOptional.get();
        if(news.getDeletedAt()!=null) return null;

        return news;
    }
}
