package com.gsvn.hrmservice.model.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class StaffRequest {
    @NotBlank(message = "FULL_NAME_REQUIRED")
    private String fullName;

    @NotBlank(message = "EMAIL_REQUIRED")
    @Email(message = "INVALID_EMAIL_FORMAT")
    private String email;

    @NotNull(message = "DOB_REQUIRED")
    @Past(message = "DOB_MUST_BE_IN_PAST")
    private LocalDate dob;
    @NotBlank(message = "ADDRESS_REQUIRED")
    @Size(max = 255, message = "ADDRESS_TOO_LONG")
    private String address;
    @Pattern(regexp = "^(MALE|FEMALE|OTHER)$", message = "INVALID_GENDER")
    private String gender;

    @Pattern(regexp = "^[0-9]{10}$", message = "PHONE_NUMBER_INVALID_FORMAT")
    private String phoneNumber;

    @NotBlank(message = "IDENTITY_CARD_REQUIRED")
    @Size(min = 9, max = 12, message = "IDENTITY_CARD_INVALID_SIZE")
    private String identityCard;

    private Integer warehouseId;
}