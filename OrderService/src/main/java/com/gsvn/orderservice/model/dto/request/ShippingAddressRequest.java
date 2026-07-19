package com.gsvn.orderservice.model.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ShippingAddressRequest {

    @NotBlank(message = "RECEIVER_NAME_REQUIRED")
    private String receiverName;

    @NotBlank(message = "RECEIVER_PHONE_REQUIRED")
    @Pattern(regexp = "^(0|\\+84)[0-9]{9,10}$", message = "PHONE_INVALID")
    private String receiverPhone;

    @Email(message = "EMAIL_INVALID")
    private String email;

    private String provinceId;
    private String wardId;
    private String addressDetail;
}