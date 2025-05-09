package com.tgb.gsvnbackend.model.mapper;

import com.tgb.gsvnbackend.model.dto.SPUDTO;
import com.tgb.gsvnbackend.model.entity.SPU;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface SPUMapper {

    SPUDTO toDTO(SPU spu);

    SPU toEntity(SPUDTO spuDTO);
}
