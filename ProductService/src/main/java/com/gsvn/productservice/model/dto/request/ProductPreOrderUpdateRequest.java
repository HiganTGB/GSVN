package com.gsvn.productservice.model.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class ProductPreOrderUpdateRequest {

    @NotBlank(message = "PRE_ORDER_NAME_REQUIRED")
    private String preName;

    @NotNull(message = "PRE_ORDER_STATUS_REQUIRED")
    private Boolean preIsActive;

    @NotNull(message = "PRE_START_DATE_REQUIRED")
    private OffsetDateTime preStartAt;

    @NotNull(message = "PRE_END_DATE_REQUIRED")
    private OffsetDateTime preEndAt;

    @Future(message = "PRE_RELEASE_DATE_MUST_BE_FUTURE")
    private LocalDate preReleaseDate;
}