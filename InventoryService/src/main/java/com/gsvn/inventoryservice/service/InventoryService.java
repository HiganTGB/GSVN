package com.gsvn.inventoryservice.service;

import com.gsvn.inventoryservice.common.PageResponse;
import com.gsvn.inventoryservice.model.dto.InventoryDTO;
import com.gsvn.inventoryservice.model.dto.SkuSellableDTO;

import java.util.List;
import java.util.Map;

public interface InventoryService {
   PageResponse<InventoryDTO> getInventoryList(Integer warehouseId, Integer skuId, int page, int size, String sortField, String sortDirection);
   Map<Long, Boolean> getSkusSellableStatus(List<Long> skuIds);
   void processReadyToPick(String skuCode, Integer warehouseId, Integer quantity);
   void processPacked(String skuCode, Integer warehouseId, Integer quantity);
   void processShipmentPacked(Integer warehouseId, Map<String, Integer> items);
    List<SkuSellableDTO> getSkusSellable(List<Long> skuIds);
}
