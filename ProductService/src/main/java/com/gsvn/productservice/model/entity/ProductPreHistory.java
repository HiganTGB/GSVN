package com.gsvn.productservice.model.entity;

import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductPreHistory {
    private Integer id;
    private Integer productId;
    private String preName;
    private OffsetDateTime preStartAt;
    private OffsetDateTime preEndAt;
    private LocalDate preReleaseDate;
    private Integer totalOrdersAchieved;
    private String skuPricesSnapshot;
    private OffsetDateTime archivedAt;
}