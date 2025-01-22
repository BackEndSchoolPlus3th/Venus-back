package com.ll.server.global.jpa;

import com.ll.server.domain.news.news.entity.News;
import com.ll.server.domain.news.news.entity.Publisher;
import com.ll.server.domain.news.news.enums.Country;
import com.ll.server.domain.news.news.repository.NewsPublisherRepository;
import com.ll.server.domain.news.news.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
    private final NewsRepository newsRepository;
    private final NewsPublisherRepository newsPublisherRepository;
    @Override
    public void run(String... args) throws Exception {
        // Publisher 객체 생성 (Country enum 값 사용)
        Publisher publisher = new Publisher("Test Publisher", Country.USA); // Publisher는 Country 값을 받습니다.
        newsPublisherRepository.save(publisher);
        // News 객체 생성
        News news = new News("Test News", "This is a test news content", publisher, "John Doe",
                "http://example.com/image.jpg", "http://example.com/thumbnail.jpg",
                "http://example.com");
        newsRepository.save(news);
    }
}