package com.gsvn.productservice.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gsvn.productservice.model.entity.Dimensions;
import com.gsvn.productservice.model.entity.SaleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailDTO {

    private Integer id;
    private String name;
    private Integer categoryId;
    private Integer brandId;
    private String description;
    private String releaseDate;
    private String imageUrl;
    private List<String> galleryImages;
    private SaleStatus saleStatus;
    private Boolean isActive;

    private String preName;
    private Boolean preIsActive;
    private OffsetDateTime preStartAt;
    private OffsetDateTime preEndAt;
    private LocalDate preReleaseDate;

    private List<SkuInternalResponse> skus;
    private List<VariantInternalResponse> variants;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SkuInternalResponse {
        private Long id;
        private String skuCode;

        @JsonFormat(shape = JsonFormat.Shape.STRING)
        private BigDecimal importPrice;

        @JsonFormat(shape = JsonFormat.Shape.STRING)
        private BigDecimal sellingPrice;

        @JsonFormat(shape = JsonFormat.Shape.STRING)
        private BigDecimal prePrice;

        @JsonFormat(shape = JsonFormat.Shape.STRING)
        private BigDecimal preDepositAmount;

        private Integer prePerQty;
        private Integer weightGram;
        private Dimensions dimensionsCm;
        private Integer preLimitQuantity;
        private Boolean isActive;
        private OffsetDateTime createdAt;
        private OffsetDateTime updatedAt;
        private List<Integer> optionIds;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VariantInternalResponse {
        private Long id;
        private String name;
        private List<OptionInternalResponse> options;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class OptionInternalResponse {
            private Long id;
            private String name;
        }
    }
}