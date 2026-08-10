package com.gsvn.searchservice.converter;

import com.gsvn.searchservice.model.internal.SkuSellableDTO;
import com.gsvn.searchservice.model.response.ProductDetailDTO;
import com.gsvn.searchservice.model.response.ProductFullDetailDTO;
import com.gsvn.searchservice.model.response.SaleStatus;
import com.gsvn.searchservice.model.response.DisplayStatus;
import org.springframework.stereotype.Component;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Component
public class ProductConverter {

    public ProductFullDetailDTO toFullDetail(ProductDetailDTO detail, Map<Long, SkuSellableDTO> inventoryMap) {
        if (detail == null) return null;

        List<ProductFullDetailDTO.SkuFullResponse> skuFullResponses = detail.getSkus().stream()
                .map(sku -> {
                    SkuSellableDTO inv = inventoryMap.get(sku.getId());

                    return ProductFullDetailDTO.SkuFullResponse.builder()
                            .id(sku.getId())
                            .skuCode(sku.getSkuCode())
                            .sellingPrice(sku.getSellingPrice())
                            .prePrice(sku.getPrePrice())
                            .preDepositAmount(sku.getPreDepositAmount())
                            .preLimitQuantity(sku.getPreLimitQuantity())
                            .prePerQty(sku.getPrePerQty())
                            .weightGram(sku.getWeightGram())
                            .dimensionsCm(sku.getDimensionsCm())
                            .isActive(sku.getIsActive())
                            .optionIds(sku.getOptionIds())
                            .physicalAvailable(inv != null ? inv.getPhysicalAvailable() : 0L)
                            .preOrders(inv != null ? inv.getPreOrders() : 0)
                            .isSellable(inv != null && inv.isSellable())
                            .build();
                })
                .collect(Collectors.toList());

        List<ProductFullDetailDTO.VariantFullResponse> variantFullResponses = detail.getVariants().stream()
                .map(v -> ProductFullDetailDTO.VariantFullResponse.builder()
                        .id(v.getId())
                        .name(v.getName())
                        .options(v.getOptions().stream()
                                .map(o -> ProductFullDetailDTO.VariantFullResponse.OptionFullResponse.builder()
                                        .id(o.getId())
                                        .name(o.getName())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());

        boolean anySkuSellable = skuFullResponses.stream().anyMatch(ProductFullDetailDTO.SkuFullResponse::getIsSellable);
        String displayStatus = determineProductDisplayStatus(detail.getSaleStatus(), anySkuSellable);

        return ProductFullDetailDTO.builder()
                .id(detail.getId())
                .name(detail.getName())
                .categoryId(detail.getCategoryId())
                .brandId(detail.getBrandId())
                .description(detail.getDescription())
                .releaseDate(detail.getReleaseDate())
                .imageUrl(detail.getImageUrl())
                .galleryImages(detail.getGalleryImages())
                .saleStatus(detail.getSaleStatus())
                .isActive(detail.getIsActive())
                .displayStatus(displayStatus)
                .preName(detail.getPreName())
                .preIsActive(detail.getPreIsActive())
                .preStartAt(detail.getPreStartAt())
                .preEndAt(detail.getPreEndAt())
                .preReleaseDate(detail.getPreReleaseDate())
                .skus(skuFullResponses)
                .variants(variantFullResponses)
                .build();
    }

    private String determineProductDisplayStatus(SaleStatus saleStatus, boolean hasStock) {
        if (!hasStock) return DisplayStatus.OUT_OF_STOCK.name();
        return switch (saleStatus) {
            case RUMOR -> DisplayStatus.COMING_SOON.name();
            case COMING_SOON -> DisplayStatus.COMING_SOON.name();
            case PREORDER_OPEN -> DisplayStatus.PREORDER_OPEN.name();
            case PREORDER_CLOSED -> DisplayStatus.PREORDER_CLOSED.name();
            case AVAILABLE -> DisplayStatus.IN_STOCK.name();
            default -> DisplayStatus.IN_STOCK.name();
        };
    }
}