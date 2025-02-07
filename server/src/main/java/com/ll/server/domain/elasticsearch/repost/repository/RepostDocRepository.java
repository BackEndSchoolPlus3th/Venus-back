package com.ll.server.domain.elasticsearch.repost.repository;

import com.ll.server.domain.elasticsearch.repost.doc.RepostDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface RepostDocRepository extends ElasticsearchRepository<RepostDoc,Long> {

}
