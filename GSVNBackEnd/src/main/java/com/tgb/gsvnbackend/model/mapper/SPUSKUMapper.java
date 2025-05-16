package com.tgb.gsvnbackend.model.mapper;

import com.tgb.gsvnbackend.model.entity.SPUSKU;
import com.tgb.gsvnbackend.model.dto.SPUSKUDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface SPUSKUMapper {

    SPUSKUDTO toDTO(SPUSKU spuSKU);
    @Mapping(target = "isDeleted",ignore = true)
    SPUSKU toEntity(SPUSKUDTO spuSKUDTO);
}