package com.gsvn.cartservice.model.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class GuestCartRequest {
    private List<GuestCartItem> items;

    @Data
    public static class GuestCartItem {
        private Long skuId;
        private Integer quantity;
        private Boolean isDeposit;
    }
}