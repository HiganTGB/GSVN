package com.tgb.gsvnbackend.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BrandDTO {
    private Integer brandId;
    @NotEmpty(message = "Title is required")
    @Size(min = 2, max = 255, message = "Title must be between 2 and 255 characters")
    private String title;

    @NotEmpty(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
}