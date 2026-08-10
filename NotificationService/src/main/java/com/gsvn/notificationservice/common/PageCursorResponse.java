package com.gsvn.notificationservice.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageCursorResponse<T> {
    private List<T> content;
    private String nextCursor;
    private Boolean hasMore;
    public static <T> PageCursorResponse<T> of(List<T> rawData, int limit, Function<T, String> cursorExtractor) {
        boolean hasMore = rawData != null && rawData.size() > limit;

        List<T> data = new ArrayList<>();
        String nextCursor = null;

        if (rawData != null && !rawData.isEmpty()) {
            data = hasMore ? rawData.subList(0, limit) : rawData;
            if (hasMore && !data.isEmpty()) {
                T lastItem = data.get(data.size() - 1);
                nextCursor = cursorExtractor.apply(lastItem);
            }
        }

        return PageCursorResponse.<T>builder()
                .content(data)
                .nextCursor(nextCursor)
                .hasMore(hasMore)
                .build();
    }
}