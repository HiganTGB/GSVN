package com.tgb.gsvnbackend.model.mapper;

import com.tgb.gsvnbackend.model.dto.SKUAttributeDTO;
import com.tgb.gsvnbackend.model.entity.SKUAttribute;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface SKUAttributeMapper {

    SKUAttributeDTO toDTO(SKUAttribute skuAttribute);

    SKUAttribute toEntity(SKUAttributeDTO skuAttributeDTO);
}
