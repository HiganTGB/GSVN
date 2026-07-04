package com.gsvn.productservice.model.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.OffsetDateTime;
@Data
@Builder
public class ProductPreCampaignResponse {
    private String preName;
    private Boolean preIsActive;

    private OffsetDateTime preStartAt;

    private OffsetDateTime preEndAt;

    private LocalDate preReleaseDate;
}
