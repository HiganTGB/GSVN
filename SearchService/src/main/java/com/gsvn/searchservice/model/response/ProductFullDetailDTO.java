package com.gsvn.searchservice.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class ProductFullDetailDTO {


    private Integer id;
    private String name;
    private Integer categoryId;
    private Integer brandId;
    private String description;
    private String releaseDate;
    private String imageUrl;
    private List<String> galleryImages;
    private SaleStatus saleStatus;
    private String displayStatus;
    private Boolean isActive;


    private String preName;
    private Boolean preIsActive;
    private OffsetDateTime preStartAt;
    private OffsetDateTime preEndAt;
    @JsonFormat(pattern = "MM-yyyy")
    private LocalDate preReleaseDate;


    private List<SkuFullResponse> skus;
    private List<VariantFullResponse> variants;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SkuFullResponse {
        private Long id;
        private String skuCode;


        private BigDecimal sellingPrice;

        private BigDecimal prePrice;

        private BigDecimal preDepositAmount;


        private Integer preLimitQuantity;
        private Integer prePerQty;

        private Integer weightGram;
        private Dimensions dimensionsCm;

        private Long physicalAvailable;
        private Integer preOrders;
        private Boolean isSellable;

        private Boolean isActive;
        private List<Integer> optionIds;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VariantFullResponse {
        private Long id;
        private String name;
        private List<OptionFullResponse> options;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class OptionFullResponse {
            private Long id;
            private String name;
        }
    }
}