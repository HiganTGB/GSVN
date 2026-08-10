package com.gsvn.inventoryservice.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseResponse {
    private Integer id;
    private String name;
    private String code;
    private Integer staffId;
    private Boolean isActive;

    private String contactName;
    private String contactPhone;
    private String addressDetail;

    private String provinceCode;
    private String wardCode;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}