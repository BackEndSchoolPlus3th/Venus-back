package com.ll.server.domain.elasticsearch.repost.service;

import com.ll.server.domain.elasticsearch.repost.doc.RepostDoc;
import com.ll.server.domain.elasticsearch.repost.repository.RepostDocRepository;
import com.ll.server.domain.repost.dto.RepostDTO;
import com.ll.server.domain.repost.entity.Repost;
import com.ll.server.domain.repost.repository.RepostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RepostDocService {
    private final RepostDocRepository repostDocRepository;
    private final RepostRepository repostRepository;

    @Transactional(readOnly = true)
    public Page<RepostDTO> searchContent(String keyword, Pageable pageable){
        Page<RepostDoc> results=repostDocRepository.searchByContent(keyword,pageable);
        List<Long> ids=results.getContent().stream().map(RepostDoc::getId).toList();

        Pageable forJPA= PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("id").descending());

        Page<Repost> realResult=repostRepository.findAllByIdIn(ids,forJPA);
        return new PageImpl<>(
                realResult
                .map(RepostDTO::new)
                        .stream().collect(Collectors.toList()),
                results.getPageable(),
                results.getTotalElements()
                );

    }
}
