package com.gsvn.inventoryservice.model.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierRequest {

    @NotBlank(message = "SUPPLIER_NAME_REQUIRED")
    @Size(max = 255, message = "SUPPLIER_NAME_TOO_LONG")
    private String name;

    @Size(max = 100, message = "CONTACT_NAME_TOO_LONG")
    private String contactName;

    @Size(max = 20, message = "PHONE_TOO_LONG")
    @Pattern(regexp = "^(0|\\+84)(\\s|\\.)?((3[2-9])|(5[689])|(7[06-9])|(8[1-689])|(9[0-46-9]))(\\d)(\\s|\\.)?(\\d{3})(\\s|\\.)?(\\d{3})$",
            message = "CONTACT_PHONE_INVALID")
    private String phone;

    @Email(message = "EMAIL_INVALID")
    @Size(max = 100, message = "EMAIL_TOO_LONG")
    private String email;

    @NotBlank(message = "TAX_CODE_REQUIRED")
    @Size(max = 50, message = "TAX_CODE_TOO_LONG")
    private String taxCode;

    private Boolean isActive;

    private String note;
}