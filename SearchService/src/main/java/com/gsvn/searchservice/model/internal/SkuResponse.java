package com.gsvn.searchservice.model.internal;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gsvn.searchservice.model.response.Dimensions;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkuResponse {
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