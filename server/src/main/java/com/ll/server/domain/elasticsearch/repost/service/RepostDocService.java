package com.ll.server.domain.elasticsearch.repost.service;

import com.ll.server.domain.elasticsearch.repost.doc.RepostDoc;
import com.ll.server.domain.elasticsearch.repost.repository.RepostDocRepository;
import com.ll.server.domain.repost.dto.RepostOnly;
import com.ll.server.domain.repost.entity.Repost;
import com.ll.server.domain.repost.repository.RepostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public Page<RepostOnly> searchContent(String keyword, Pageable pageable){
        Page<RepostDoc> results=repostDocRepository.searchByContent(keyword,pageable);
        List<Long> ids=results.getContent().stream().map(RepostDoc::getId).toList();

        List<Repost> realResult=repostRepository.findAllByIdIn(ids,Sort.by("id").descending());
        return new PageImpl<>(
                realResult.stream().filter(repost -> repost.getDeletedAt()==null)
                .map(RepostOnly::new)
                        .collect(Collectors.toList()),
                results.getPageable(),
                results.getTotalElements()
                );

    }
}
