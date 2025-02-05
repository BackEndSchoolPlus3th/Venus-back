package com.ll.server.domain.elasticsearch.news.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.ll.server.domain.elasticsearch.news.doc.NewsDoc;
import com.ll.server.domain.elasticsearch.news.repository.NewsDocRepository;
import com.ll.server.domain.news.news.dto.NewsDTO;
import com.ll.server.domain.news.news.entity.News;
import com.ll.server.domain.news.news.repository.NewsRepository;
import com.ll.server.global.config.ElasticSearchClientConfig;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewsDocService {
    private final NewsDocRepository newsDocRepository;
    private final NewsRepository newsRepository;

    @SneakyThrows
    @Transactional(readOnly=true)
    public Page<NewsDTO> search(String keyword, boolean hasTitle, boolean hasContent, boolean hasPublisher, String category, Pageable page){
        ElasticsearchClient client = new ElasticSearchClientConfig().createElasticsearchClient();
        BoolQuery.Builder bqBuilder=new BoolQuery.Builder();

        bqBuilder.mustNot(mn->mn.exists(ex->ex.field("deleted_at")));

        boolean hasShould = hasTitle || hasContent || hasPublisher;

        if(hasTitle){
            bqBuilder.should(s->s.match(m->m.field("title").query(keyword)));
        }

        if(hasContent){
            bqBuilder.should(s->s.match(m->m.field("content").query(keyword)));
        }

        if(hasPublisher){
            bqBuilder.should(s->s.match(m->m.field("publisher").query(keyword)));
        }

        if(!category.isBlank()){
            bqBuilder.must(s->s.match(m->m.field("category").query(category)));
        }

        if(hasShould){
            bqBuilder.minimumShouldMatch("1");
        }

        SearchResponse<NewsDoc> result= client.search(
                SearchRequest.of(
                        s->s.index("news")
                                .from(page.getPageNumber())
                                .size(page.getPageSize())
                                .query(
                                        q->q.bool(bqBuilder.build())
                                )
                ),NewsDoc.class
        );
        List<Long> ids=result.hits().hits().stream().map(hit-> Objects.requireNonNull(hit.source()).getId()).toList();

        Pageable forJPA= PageRequest.of(page.getPageNumber(), page.getPageSize(), Sort.by("id").descending());

        Page<News> realResult=newsRepository.findAllByIdIn(ids,forJPA);

        //NewsDTO 빌더로 하나하나 다 넣음
         return new PageImpl<>(
                        realResult.getContent().stream()
                        .map(NewsDTO::new)
                                .collect(Collectors.toList()),
                 realResult.getPageable(),
                 realResult.getTotalElements()
         );

    }

}
