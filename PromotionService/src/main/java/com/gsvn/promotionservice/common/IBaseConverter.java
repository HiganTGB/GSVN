package com.gsvn.promotionservice.common;

import java.util.List;
import java.util.stream.Collectors;

public interface IBaseConverter<E, REQ, RES> {
    E toEntity(REQ request);

    RES toResponse(E entity);

    default List<RES> toResponseList(List<E> entities) {
        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}