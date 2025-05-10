package com.tgb.gsvnbackend.model.mapper;

import com.tgb.gsvnbackend.model.dto.SKUDTO;
import com.tgb.gsvnbackend.model.entity.SKU;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SKUMapper {

    SKU toEntity(SKUDTO dto);

    @Mapping(target = "spuId", ignore = true)
    @Mapping(target = "attrs", ignore = true)
    SKUDTO toDTO(SKU entity);
}
