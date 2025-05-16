package com.tgb.gsvnbackend.model.mapper;

import com.tgb.gsvnbackend.model.domain.SPUDomain;
import com.tgb.gsvnbackend.model.dto.SPUDTO;
import com.tgb.gsvnbackend.model.entity.SPU;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface SPUMapper {

    SPUDTO toDTO(SPU spu);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "spuId", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "attrs")
    SPU toEntity(SPUDTO spuDTO);

    SPUDomain toDomain(SPU spu);


}
