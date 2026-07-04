package com.gsvn.productservice.model.dto.request;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantSyncRequest {
    @Valid
    @NotEmpty(message = "VARIANT_LIST_REQUIRED")
    private List<VariantUpdateDto> variants;
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VariantUpdateDto {
        /**
         * Nếu id != null: Đây là Variant cũ (Cần so sánh để Update hoặc giữ nguyên)
         * Nếu id == null: Đây là Variant mới hoàn toàn
         */
        private Long id;

        @NotBlank(message = "VARIANT_NAME_REQUIRED")
        @Size(max = 50, message = "VARIANT_NAME_TOO_LONG")
        private String name;

        @Valid
        @NotEmpty(message = "OPTION_LIST_REQUIRED")
        private List<OptionUpdateDto> options;
    }
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OptionUpdateDto {
        /**
         * Nếu id != null: Update Option cũ
         * Nếu id == null: Thêm Option mới vào Variant này
         */
        private Long id;

        @NotBlank(message = "OPTION_NAME_REQUIRED")
        @Size(max = 50, message = "OPTION_NAME_TOO_LONG")
        private String name;
    }
}