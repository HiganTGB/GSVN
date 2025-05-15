package com.tgb.gsvnbackend.service;

import com.tgb.gsvnbackend.model.domain.LineItemDomain;
import java.util.List;

public interface InventoryService {
    void decreaseStock(String orderId, List<LineItemDomain> lineItems);
    void increaseStock(String orderId, List<LineItemDomain> lineItems);
}