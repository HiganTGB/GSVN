package com.gsvn.shipmentservice.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentItem {
    private Long id;
    private Long shipmentId;
    private String skuCode;
    private Integer quantity;
    private String productName;
}