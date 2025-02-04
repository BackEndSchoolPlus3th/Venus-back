package com.ll.server.global.response.response;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class CustomPage<T> {
    private List<T> contents;

    private int pageNumber;
    private int pageSize;
    private int totalPages;
    private long totalCount;

    public static <T> CustomPage<T> of(Page<T> pagedContents) {
        CustomPage<T> converted = new CustomPage<>();
        converted.contents = pagedContents.getContent();
        converted.pageNumber = pagedContents.getNumber();
        converted.pageSize = pagedContents.getSize();
        converted.totalPages = pagedContents.getTotalPages();
        converted.totalCount = pagedContents.getTotalElements();
        return converted;
    }
}
