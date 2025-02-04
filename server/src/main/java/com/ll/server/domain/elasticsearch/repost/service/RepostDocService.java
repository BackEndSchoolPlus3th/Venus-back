package com.ll.server.domain.elasticsearch.repost.service;

import com.ll.server.domain.elasticsearch.repost.doc.RepostDoc;
import com.ll.server.domain.elasticsearch.repost.repository.RepostDocRepository;
import com.ll.server.domain.repost.dto.RepostDTO;
import com.ll.server.domain.repost.repository.RepostRepository;
import lombok.RequiredArgsConstructor;
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
    public List<RepostDTO> searchContent(String keyword){
        List<RepostDoc> results=repostDocRepository.searchByContent(keyword);
        List<Long> ids=results.stream().map(RepostDoc::getId).toList();

        return repostRepository.findAllById(ids).stream()
                .map(RepostDTO::new)
                .collect(Collectors.toList());
    }
}
