package com.ll.server.global.jpa;

import com.ll.server.domain.news.news.entity.News;
import com.ll.server.domain.news.news.enums.NewsCategory;
import com.ll.server.domain.news.news.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
    private final NewsRepository newsRepository;
    @Override
    public void run(String... args) throws Exception {
        // News 객체 생성
        News news = new News("Test News", "This is a test news content", "example publisher", "John Doe",
                "http://example.com/image.jpg", "http://example.com/thumbnail.jpg",
                "http://example.com", NewsCategory.ECONOMY, "2021-09-01T00:00:00");
        newsRepository.save(news);
    }
}