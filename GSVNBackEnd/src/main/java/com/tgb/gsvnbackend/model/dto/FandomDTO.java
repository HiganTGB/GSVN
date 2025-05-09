package com.tgb.gsvnbackend.model.dto;

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
public class FandomDTO {
    private Integer id;

    @NotEmpty(message = "Fandom title is required")
    @Size(min = 2, max = 255, message = "Fandom title must be between 2 and 255 characters")
    private String title;
}
