package com.gsvn.inventoryservice.model.entity;

import lombok.*;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Supplier {
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