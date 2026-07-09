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
public class WarehouseRequest {

    @NotBlank(message = "WAREHOUSE_NAME_REQUIRED")
    @Size(max = 100, message = "WAREHOUSE_NAME_TOO_LONG")
    private String name;

    @NotBlank(message = "WAREHOUSE_CODE_REQUIRED")
    @Size(max = 50, message = "WAREHOUSE_CODE_TOO_LONG")
    @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "WAREHOUSE_CODE_INVALID_FORMAT")
    private String code;
    @NotNull(message = "MANAGER_REQUIRED")
    private Integer staffId;

    private Boolean isActive;

    @Size(max = 100, message = "CONTACT_NAME_TOO_LONG")
    private String contactName;

    @NotBlank(message = "CONTACT_PHONE_REQUIRED")
    @Pattern(regexp = "^(0|\\+84)(\\s|\\.)?((3[2-9])|(5[689])|(7[06-9])|(8[1-689])|(9[0-46-9]))(\\d)(\\s|\\.)?(\\d{3})(\\s|\\.)?(\\d{3})$",
            message = "CONTACT_PHONE_INVALID")
    private String contactPhone;

    @Size(max = 255, message = "ADDRESS_DETAIL_TOO_LONG")
    private String addressDetail;

    @NotBlank(message = "PROVINCE_REQUIRED")
    private String provinceCode;

    @NotBlank(message = "WARD_REQUIRED")
    private String wardCode;

}