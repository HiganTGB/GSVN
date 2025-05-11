package com.tgb.gsvnbackend.model.mapper;

import com.tgb.gsvnbackend.model.dto.FandomDTO;
import com.tgb.gsvnbackend.model.entity.Fandom;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface FandomMapper {

    FandomDTO toDTO(Fandom fandom);
    @Mapping(target = "fandomId", ignore = true)
    Fandom toEntity(FandomDTO fandomDTO);

}