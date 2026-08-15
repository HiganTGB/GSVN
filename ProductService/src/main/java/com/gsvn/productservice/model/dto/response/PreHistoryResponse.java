package com.gsvn.productservice.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreHistoryResponse {
    private Integer id;
    private Integer productId;

    private String preName;

    private OffsetDateTime preStartAt;

    private OffsetDateTime preEndAt;

    private LocalDate preReleaseDate;

    private Integer totalOrdersAchieved;

    private Object skuPricesSnapshot;

    private OffsetDateTime archivedAt;
}