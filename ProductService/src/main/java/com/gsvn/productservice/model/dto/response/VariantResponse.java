package com.gsvn.productservice.model.dto.response;


import lombok.Data;
import java.util.List;

@Data
public class VariantResponse {
    private Long id;
    private String name;
    private List<OptionResponse> options;
    @Data
    public static class OptionResponse {
        private Long id;
        private String name;
    }
}

