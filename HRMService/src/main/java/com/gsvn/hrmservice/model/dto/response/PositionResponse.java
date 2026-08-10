package com.gsvn.hrmservice.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.math.BigDecimal;
import java.time.OffsetDateTime;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PositionResponse {
    private Integer positionId;
    private String positionName;
    private BigDecimal defaultBaseSalary;
    private String description;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
