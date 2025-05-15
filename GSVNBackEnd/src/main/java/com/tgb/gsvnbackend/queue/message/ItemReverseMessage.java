package com.tgb.gsvnbackend.queue.message;

import com.tgb.gsvnbackend.model.domain.CartItemDomain;
import com.tgb.gsvnbackend.model.domain.LineItemDomain;
import com.tgb.gsvnbackend.model.entity.CartItem;

import java.util.List;

public record ItemReverseMessage(String orderId,List<LineItemDomain> lineItems) {

}
