package com.gsvn.cartservice.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Wishlist {
    private Integer id;
    private Long customerId;
    private Integer productId;
    private OffsetDateTime createdAt;
}