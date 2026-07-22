package com.gsvn.shipmentservice.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Shipment {
    private Long id;
    private String shipmentCode;
    private String orderCode;
    private String warehouseCode;

    private String deliveryMethod;
    private String status;

    private LocalDate scheduledDate;

    private BigDecimal totalCodAmount;
    private BigDecimal actualShippingCost;
    private String trackingNumber;

    // Thông tin người nhận
    private String receiverName;
    private String receiverPhone;
    private String receiverEmail;
    private String provinceCode;
    private String wardCode;
    private String addressDetail;
    private String customerNote;

    private Integer totalWeight;
    private Integer length;
    private Integer width;
    private Integer height;



    // Thông tin mapping với đối tác vận chuyển
    private String partnerProvinceCode;
    private String partnerDistrictCode;
    private String partnerWardCode;

    private Long confirmedBy;
    private OffsetDateTime confirmedAt;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}