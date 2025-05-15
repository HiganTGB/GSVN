package com.tgb.gsvnbackend.queue.message;

import com.tgb.gsvnbackend.model.domain.CartItemDomain;
import com.tgb.gsvnbackend.model.entity.CartItem;
import com.tgb.gsvnbackend.model.enumeration.CartStatus;
import com.tgb.gsvnbackend.model.enumeration.PaymentMethod;


import java.util.List;

public record OrderInitMessage(String cardId,
                               String userId,
                               String note,
                               List<CartItemDomain> cartItems,
                               String receiver,
                               String street,
                               String city,
                               String state,
                               String zip,
                               String phone,
                               String paymentMethod,
                               String ipAddress) {
}
