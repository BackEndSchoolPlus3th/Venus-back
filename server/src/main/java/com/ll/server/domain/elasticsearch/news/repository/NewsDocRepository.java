package com.ll.server.domain.elasticsearch.news.repository;

import com.ll.server.domain.elasticsearch.news.doc.NewsDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface NewsDocRepository extends ElasticsearchRepository<NewsDoc,Long> {
}
