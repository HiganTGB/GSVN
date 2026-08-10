package com.gsvn.shipmentservice.model.dto.response;


import com.gsvn.shipmentservice.model.entity.DeliveryMethod;
import com.gsvn.shipmentservice.model.entity.ShipmentStatus;
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
public class ShipmentResponse {
    private Long id;
    private String shipmentCode;
    private String orderCode;
    private String warehouseCode;

    private String deliveryMethod;
    private ShipmentStatus status;
    private LocalDate scheduledDate;


    private BigDecimal totalCodAmount;
    private BigDecimal actualShippingCost;
    private String trackingNumber;


    private Integer totalWeight;
    private Integer length;
    private Integer width;
    private Integer height;


    private String receiverName;
    private String receiverPhone;
    private String receiverEmail;

    private String provinceCode;
    private String wardCode;
    private String addressDetail;
    private String customerNote;

    private String parentProvinceCode;
    private String parentDistrictCode;
    private String parentWardCode;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}