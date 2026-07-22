package com.gsvn.shipmentservice.converter;

import com.gsvn.shipmentservice.model.dto.GHNOrderRequest;
import com.gsvn.shipmentservice.model.entity.Shipment;
import com.gsvn.shipmentservice.model.entity.ShipmentItem;
import com.gsvn.shipmentservice.model.entity.WarehousePartner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class GHNShipmentConverter {

    public GHNOrderRequest toGHNRequest(Shipment shipment, List<ShipmentItem> items, WarehousePartner partner) {
        if (shipment == null) return null;


        List<GHNOrderRequest.GHNItem> ghnItems = items.stream()
                .map(item -> GHNOrderRequest.GHNItem.builder()
                        .name(item.getProductName())
                        .code(item.getSkuCode())
                        .quantity(item.getQuantity())
                        .price(shipment.getTotalCodAmount().intValue())
                        .build())
                .collect(Collectors.toList());

        return GHNOrderRequest.builder()
                .token(partner.getPartnerToken())
                .shopId(partner.getShopId())
                .paymentTypeId(2)
                .note(shipment.getCustomerNote())
                .requiredNote("KHONGCHOXEMHANG")
                .clientOrderCode(shipment.getOrderCode())
                .toName(shipment.getReceiverName())
                .toPhone(shipment.getReceiverPhone())
                .toAddress(shipment.getAddressDetail())
                .toWardCode(shipment.getPartnerWardCode())
                .toDistrictId(Integer.parseInt(shipment.getPartnerDistrictCode()))
                .codAmount(shipment.getTotalCodAmount().intValue())
                .content("Giao hang don: " + shipment.getOrderCode())
                .weight(shipment.getTotalWeight())
                .length(shipment.getLength())
                .width(shipment.getWidth())
                .height(shipment.getHeight())
                .insuranceValue(shipment.getTotalCodAmount().intValue())
                .serviceTypeId(2)
                .items(ghnItems)
                .build();
    }
}