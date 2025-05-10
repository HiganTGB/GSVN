package com.tgb.gsvnbackend.model.entity;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@Document(collection = "orders") // Ánh xạ tới collection có tên "orders" (bạn có thể đặt tên khác)
public class Order extends AbstractMappedEntity {

    @Id
    private String id;
    private String userId;
    private String name;
    private String note;
    private String state;
    private LocalDate date;
    private List<LineItem> lineItems;
    private BigDecimal subTotal;
    @Data
    public static class LineItem {
        private String id;
        private String sku;
        private String name;
        private Integer quantity;
        private Pricing pricing;
    }

    @Data
    public static class Pricing {
        private BigDecimal retail;
        private BigDecimal sale;

    }
    @Data
    public static class ShippingAddress {
        private String receiver;
        private String street;
        private String city;
        private String state;
        private String zip;
        private String phone;
    }
    @Data
    public static class PaymentMethod {
        private String status;
        private String number;
    }
}