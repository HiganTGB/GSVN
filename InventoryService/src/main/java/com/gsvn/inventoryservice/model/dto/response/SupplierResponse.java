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
public class SupplierResponse {
    private Integer id;
    private String name;
    private String contactName;
    private String phone;
    private String email;
    private String taxCode;
    private Boolean isActive;
    private String note;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}