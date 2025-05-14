package com.tgb.gsvnbackend.model.entity;


import com.tgb.gsvnbackend.model.enumeration.PaymentMethod;
import com.tgb.gsvnbackend.model.enumeration.State;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;




import java.time.LocalDate;
import java.util.List;
import java.util.Map;
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@Document(collection = "orders") // Ánh xạ tới collection có tên "orders" (bạn có thể đặt tên khác)
public class Order extends AbstractMappedEntity  {
    @Id
    private String id;
    private String cartId;
    private String userId;
    private String name;
    private String note;
    private State state;
    private List<LineItem> lineItems;
    private BigDecimal subTotal;
    private ShippingAddress shippingAddress;
    private Payment payment;
    private String ipAddress;
    @Data
    @AllArgsConstructor
    public static class LineItem {
        private String id;
        private String sku;
        private String name;
        private Integer quantity;
        private Pricing pricing;
    }

    @Data
    @AllArgsConstructor
    public static class Pricing {
        private BigDecimal retail;
        private BigDecimal sale;
    }
    @Data
    @AllArgsConstructor
    public static class ShippingAddress {
        private String receiver;
        private String street;
        private String city;
        private String state;
        private String zip;
        private String phone;
    }
    @Data
    @AllArgsConstructor
    public static class Payment {
        private String method;
        private String status;
        private String paymentId;
    }
}