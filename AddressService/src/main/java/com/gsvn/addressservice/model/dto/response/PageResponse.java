package com.gsvn.addressservice.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;
    private boolean first;
    private int numberOfElements;

    public static <T> PageResponse<T> of(List<T> content, long totalElements, int pageNumber, int pageSize) {
        int totalPages = (pageSize > 0) ? (int) Math.ceil((double) totalElements / pageSize) : 0;

        return PageResponse.<T>builder()
                .content(content)
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .numberOfElements(content != null ? content.size() : 0)
                .first(pageNumber <= 1)
                .last(pageNumber >= totalPages)
                .build();
    }
}