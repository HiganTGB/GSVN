package com.gsvn.cartservice.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {
    private Integer id;
    private Long customerId;

    private List<CartItemResponse> items;

    private Integer totalQuantity;
    private BigDecimal originalTotal;
    private BigDecimal preTotal;
    private BigDecimal depositTotal;
    private BigDecimal finalTotal;
    private BigDecimal requiredTotal;
}