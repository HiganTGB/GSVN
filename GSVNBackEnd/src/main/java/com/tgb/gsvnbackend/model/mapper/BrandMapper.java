package com.tgb.gsvnbackend.model.mapper;
import com.tgb.gsvnbackend.model.dto.BrandDTO;
import com.tgb.gsvnbackend.model.entity.Brand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BrandMapper {

    BrandDTO toDTO(Brand brand);
    @Mapping(target = "brandId", ignore = true)
    Brand toEntity(BrandDTO brandDTO);
}