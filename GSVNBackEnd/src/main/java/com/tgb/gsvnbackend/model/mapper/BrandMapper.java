package com.tgb.gsvnbackend.model.mapper;
import com.tgb.gsvnbackend.model.dto.BrandDTO;
import com.tgb.gsvnbackend.model.entity.Brand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BrandMapper {

    BrandDTO toDTO(Brand brand);

    Brand toEntity(BrandDTO brandDTO);
}