package com.gsvn.productservice.mapper;


import com.gsvn.productservice.model.dto.SkuSearchResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SkuSearchMapper {
    List<SkuSearchResponse> quickSearchSku(@Param("keyword") String keyword);
    List<SkuSearchResponse> findByIds(@Param("skuIds") List<Long> skuIds);
}