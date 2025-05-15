package com.tgb.gsvnbackend.model.dto;

import com.tgb.gsvnbackend.model.enumeration.CartStatus;

public record CartStatusDTO (String cartId,
                             String orderId,
                             CartStatus cartStatus,
                             String urlPayment) {
}
