package com.gsvn.productservice.model.dto.response;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
    private Integer id;
    private String name;
    private String description;
    private Integer parentCategoryId;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private List<CategoryResponse> children;
}