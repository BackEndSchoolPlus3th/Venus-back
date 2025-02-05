package com.ll.server.domain.elasticsearch.repost.repository;

import com.ll.server.domain.elasticsearch.repost.doc.RepostDoc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface RepostDocRepository extends ElasticsearchRepository<RepostDoc,Long> {

    @Query("""
            {
                "bool":{
                    "must_not":{"exists":{"field":"deleted_at"}},
                    "should":[
                        {"match":{"content":"?0"}}
                    ],
                    "minimum_should_match": 1
                }
            }
            """)
    Page<RepostDoc> searchByContent(String keyword, Pageable pageable);
}
