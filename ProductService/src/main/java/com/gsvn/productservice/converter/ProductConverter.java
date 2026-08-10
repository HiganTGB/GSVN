package com.gsvn.productservice.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gsvn.productservice.model.dto.ProductCardDTO;
import com.gsvn.productservice.model.dto.request.ProductPreOrderUpdateRequest;
import com.gsvn.productservice.model.dto.request.ProductCreateRequest;
import com.gsvn.productservice.model.dto.request.ProductBasicUpdateRequest;
import com.gsvn.productservice.model.dto.response.*;
import com.gsvn.productservice.model.dto.response.ProductBasicResponse;
import com.gsvn.productservice.model.dto.response.ProductPreCampaignResponse;

import com.gsvn.productservice.model.dto.ProductDetailDTO;
import com.gsvn.productservice.model.entity.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ProductConverter {
    private final ObjectMapper objectMapper;

    public ProductConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Product toEntity(ProductCreateRequest request) {
        if (request == null) return null;
        return Product.builder()
                .name(request.getName())
                .categoryId(request.getCategoryId())
                .brandId(request.getBrandId())
                .description(request.getDescription())
                .releaseDate(request.getReleaseDate())
                .saleStatus(SaleStatus.RUMOR)
                .isActive(true)
                .build();
    }

    public void updateBasicEntity(ProductBasicUpdateRequest request, Product entity) {
        if (request == null || entity == null) return;
        entity.setName(request.getName());
        entity.setCategoryId(request.getCategoryId());
        entity.setBrandId(request.getBrandId());
        entity.setDescription(request.getDescription());
        entity.setReleaseDate(request.getReleaseDate());
        entity.setIsActive(request.getIsActive());
    }
    public void updateCampaignEntity(ProductPreOrderUpdateRequest request, Product entity) {
        if (request == null || entity == null) return;

        entity.setPreName(request.getPreName());
        entity.setPreIsActive(request.getPreIsActive());
        entity.setPreStartAt(request.getPreStartAt());
        entity.setPreEndAt(request.getPreEndAt());
        entity.setPreReleaseDate(request.getPreReleaseDate());
    }


    public PreHistoryResponse toResponse(ProductPreHistory entity) {
        if (entity == null) return null;
        return PreHistoryResponse.builder()
                .id(entity.getId())
                .productId(entity.getProductId())
                .preName(entity.getPreName())
                .preStartAt(entity.getPreStartAt())
                .preEndAt(entity.getPreEndAt())
                .preReleaseDate(entity.getPreReleaseDate())
                .totalOrdersAchieved(entity.getTotalOrdersAchieved())
                .skuPricesSnapshot(entity.getSkuPricesSnapshot())
                .archivedAt(entity.getArchivedAt())
                .build();
    }

    public List<PreHistoryResponse> toHistoryResponseList(List<ProductPreHistory> entities) {
        if (entities == null) return List.of();
        return entities.stream().map(this::toResponse).collect(Collectors.toList());
    }
    public ProductBasicResponse toBasicResponse(Product product)
    {
        return ProductBasicResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .categoryId(product.getCategoryId())
                .brandId(product.getBrandId())
                .description(product.getDescription())
                .releaseDate(product.getReleaseDate())
                .imageUrl(product.getImageUrl())
                .saleStatus(product.getSaleStatus())
                .isActive(product.getIsActive())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
    public ProductPreCampaignResponse toPreCampaignResponse(Product product)
    {
        return ProductPreCampaignResponse.builder()
                .preName(product.getPreName())
                .preIsActive(product.getPreIsActive())
                .preStartAt(product.getPreStartAt())
                .preEndAt(product.getPreEndAt())
                .preReleaseDate(product.getPreReleaseDate())
                .build();
    }
    public ProductDetailDTO toDetailDTO(
            Product product,
            List<SkuResponse> skus,
            List<VariantResponse> variants) {

        if (product == null) return null;

        List<ProductDetailDTO.SkuInternalResponse> internalSkus = null;
        if (skus != null) {
            internalSkus = skus.stream()
                    .map(this::toSkuInternal)
                    .collect(Collectors.toList());
        }

        List<ProductDetailDTO.VariantInternalResponse> internalVariants = null;
        if (variants != null) {
            internalVariants = variants.stream()
                    .map(this::toVariantInternal)
                    .collect(Collectors.toList());
        }

        return ProductDetailDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .categoryId(product.getCategoryId())
                .brandId(product.getBrandId())
                .description(product.getDescription())
                .releaseDate(product.getReleaseDate())
                .imageUrl(product.getImageUrl())
                .galleryImages(product.getGalleryImages())
                .saleStatus(product.getSaleStatus())
                .isActive(product.getIsActive())
                .preName(product.getPreName())
                .preIsActive(product.getPreIsActive())
                .preStartAt(product.getPreStartAt())
                .preEndAt(product.getPreEndAt())
                .preReleaseDate(product.getPreReleaseDate())
                .skus(internalSkus)
                .variants(internalVariants)
                .build();
    }
    private ProductDetailDTO.SkuInternalResponse toSkuInternal(SkuResponse sku) {
        if (sku == null) return null;
        return ProductDetailDTO.SkuInternalResponse.builder()
                .id(sku.getId())
                .skuCode(sku.getSkuCode())
                .sellingPrice(sku.getSellingPrice())
                .prePrice(sku.getPrePrice())
                .preDepositAmount(sku.getPreDepositAmount())
                .prePerQty(sku.getPrePerQty())
                .weightGram(sku.getWeightGram())
                .dimensionsCm(sku.getDimensionsCm())
                .preLimitQuantity(sku.getPreLimitQuantity())
                .isActive(sku.getIsActive())
                .createdAt(sku.getCreatedAt())
                .updatedAt(sku.getUpdatedAt())
                .optionIds(sku.getOptionIds())
                .build();
    }

    private ProductDetailDTO.VariantInternalResponse toVariantInternal(VariantResponse variant) {
        if (variant == null) return null;

        List<ProductDetailDTO.VariantInternalResponse.OptionInternalResponse> internalOptions = null;
        if (variant.getOptions() != null) {
            internalOptions = variant.getOptions().stream()
                    .map(opt -> ProductDetailDTO.VariantInternalResponse.OptionInternalResponse.builder()
                            .id(opt.getId())
                            .name(opt.getName())
                            .build())
                    .collect(Collectors.toList());
        }

        return ProductDetailDTO.VariantInternalResponse.builder()
                .id(variant.getId())
                .name(variant.getName())
                .options(internalOptions)
                .build();
    }





    public ProductCardDTO toCardDTO(Product product, List<Sku> skus) {
        if (product == null) return null;

        BigDecimal minPrice = null;
        BigDecimal maxPrice = null;

        if (skus != null && !skus.isEmpty()) {
            minPrice = skus.stream()
                    .map(sku -> (product.getPreIsActive() != null && product.getPreIsActive() && sku.getPrePrice() != null)
                            ? sku.getPrePrice() : sku.getSellingPrice())
                    .filter(price -> price != null)
                    .min(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO);

            maxPrice = skus.stream()
                    .map(sku -> (product.getPreIsActive() != null && product.getPreIsActive() && sku.getPrePrice() != null)
                            ? sku.getPrePrice() : sku.getSellingPrice())
                    .filter(price -> price != null)
                    .max(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO);
        }

        return ProductCardDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .imageUrl(product.getImageUrl())
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .saleStatus(product.getSaleStatus())
                .isActive(product.getIsActive())
                .preName(product.getPreName())
                .preIsActive(product.getPreIsActive())
                .build();
    }
    public List<ProductCardDTO> toCardDTOList(List<Product> products, Map<Integer, List<Sku>> skuMap) {
        if (products == null) return List.of();
        return products.stream()
                .map(product -> toCardDTO(product, skuMap.get(product.getId())))
                .collect(Collectors.toList());
    }
}