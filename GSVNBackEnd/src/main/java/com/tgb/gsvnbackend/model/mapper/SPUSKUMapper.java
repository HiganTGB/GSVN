package com.tgb.gsvnbackend.model.mapper;

import com.tgb.gsvnbackend.model.entity.SPUSKU;
import com.tgb.gsvnbackend.model.dto.SPUSKUDTO;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface SPUSKUMapper {

    SPUSKUDTO toDTO(SPUSKU spuSKU);

    SPUSKU toEntity(SPUSKUDTO spuSKUDTO);
}