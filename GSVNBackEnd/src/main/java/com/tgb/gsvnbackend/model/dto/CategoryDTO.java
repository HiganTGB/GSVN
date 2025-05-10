package com.tgb.gsvnbackend.model.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    private Integer category_id;

    @NotEmpty(message = "Title is required")
    @Size(min = 2, max = 255, message = "Title must be between 2 and 255 characters")
    private String title;

    private int parentID;
    private List<CategoryDTO> children;
}
