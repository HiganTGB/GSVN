package com.gsvn.orderservice.model.dto.request;

import com.gsvn.orderservice.model.enums.DeliveryMethod;
import com.gsvn.orderservice.model.enums.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateRequest {

    @NotBlank(message = "TRANSACTION_ID_REQUIRED")
    private String transactionId;


    private Long customerId;


    private String warehouseCode;

    @NotBlank(message = "RECEIVER_NAME_REQUIRED")
    private String receiverName;

    @NotBlank(message = "RECEIVER_PHONE_REQUIRED")
    @Pattern(regexp = "^[0-9]{10}$", message = "INVALID_PHONE_FORMAT")
    private String receiverPhone;
    @Email(message = "INVALID_EMAIL_FORMAT")
    private String receiverEmail;

    private String provinceCode;
    private String wardCode;
    private String addressDetail;
    private String customerNote;

    @NotNull(message = "PAYMENT_METHOD_REQUIRED")
    private PaymentMethod paymentMethod;

    @NotNull(message = "DELIVERY_METHOD_REQUIRED")
    private DeliveryMethod deliveryMethod;

    private String voucherCode;

    @NotEmpty(message = "ORDER_ITEMS_REQUIRED")
    @Valid
    private List<OrderItemRequest> items;

}