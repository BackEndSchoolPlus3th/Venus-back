package com.ll.server.domain.elasticsearch.repost.repository;

import com.ll.server.domain.elasticsearch.repost.doc.RepostDoc;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface RepostDocRepository extends ElasticsearchRepository<RepostDoc,Long> {

    @Query("""
            {
                "bool":{
                    "should":[
                        {"match":{"content":"?0"}}
                    ]
                }
            }
            """)
    List<RepostDoc> searchByContent(String keyword);
}
