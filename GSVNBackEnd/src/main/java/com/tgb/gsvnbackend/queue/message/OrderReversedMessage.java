package com.tgb.gsvnbackend.queue.message;

import com.tgb.gsvnbackend.model.domain.LineItemDomain;

import java.util.List;

public record OrderReversedMessage(String orderId,
                                   Boolean status, List<LineItemDomain> lineItems)
{

}
