package com.gsvn.productservice.mapper;


import com.gsvn.productservice.model.dto.ProductSkuMapDTO;
import com.gsvn.productservice.model.dto.SkuCartDetailsDTO;
import com.gsvn.productservice.model.dto.SkuOptionMapping;
import com.gsvn.productservice.model.entity.Sku;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Optional;

@Mapper
public interface SkuMapper {
    Optional<Sku> findById(@Param("id") Long Id);
    int updateSkuInfo(Sku sku);
    int insert(Sku sku);
    List<Long> findAllIdsByProductId(@Param("productId") Integer productId);
    int insertSingleVariantMap(@Param("skuId") Long skuId, @Param("optionId") Long optionId);
    List<Sku> findByProductId(@Param("productId") Integer productId);
    long countByProductId(@Param("productId") Integer productId);
    List<Integer> findOptionIdsBySkuId(@Param("skuId") Long skuId);
    int deleteVariantMapBySkuId(@Param("skuId") Long skuId);
    Long existedBySkuCodeAndProduct(
            @Param("id") Long Id,
            @Param("skuCode") String skuCode,
            @Param("productId") Integer productId
    );
    List<SkuOptionMapping> findAllOptionMappingsByProduct(@Param("productId") Integer productId);

    List<ProductSkuMapDTO> findAllSkuIdsByProductIds(List<Integer> productIds);


    List<SkuCartDetailsDTO> findCartDetailsBySkuIds(@Param("skuIds") List<Long> skuIds);

    List<SkuCartDetailsDTO> findCartDetailsBySkuCodes(@Param("skuCodes") List<String> skuCodes);
}