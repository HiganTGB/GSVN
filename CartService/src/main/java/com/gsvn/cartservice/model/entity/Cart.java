package com.gsvn.cartservice.model.entity;

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
public class Cart {
    private Integer id;
    private Long customerId;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    private List<CartItem> items;
}