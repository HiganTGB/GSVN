package com.tgb.gsvnbackend.model.dto;

import com.tgb.gsvnbackend.model.enumeration.Status;
import com.tgb.gsvnbackend.model.enumeration.Type;
import jakarta.validation.constraints.*;
import java.util.Date;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SPUDTO {

    @NotNull(message = "SPU ID cannot be null")
    private Integer spuId;

    @NotBlank(message = "Title cannot be blank")
    @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
    private String title;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @NotNull(message = "Type cannot be null")
    private Type type;

    private Date startOrder;

    private Date endOrder;

    @Min(value = 1, message = "Category ID must be greater than or equal to 1")
    private int categoryId;

    @Min(value = 1, message = "Fandom ID must be greater than or equal to 1")
    private int fandomId;

    @Min(value = 1, message = "Brand ID must be greater than or equal to 1")
    private int brandId;

    @NotNull(message = "Status cannot be null")
    private Status status;

    private int sort;

    private boolean isDeleted;
    @NotNull(message = "Attributes cannot be null")
    private Map<String, Object> attrs;
}