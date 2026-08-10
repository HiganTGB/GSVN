package com.gsvn.inventoryservice.queue;


import com.gsvn.inventoryservice.exc.AppException;
import com.gsvn.inventoryservice.exc.ErrorCode;
import com.gsvn.inventoryservice.mapper.*;
import com.gsvn.inventoryservice.model.entity.*;
import com.gsvn.inventoryservice.model.saga.OrderEventType;
import com.gsvn.inventoryservice.queue.message.InventoryRequestMessage;
import com.gsvn.inventoryservice.queue.message.InventoryResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryInboxProcessor {

    private static final String STATUS_PROCESSED = "PROCESSED";
    private static final String STATUS_FAIL = "FAIL";
    private static final String EVENT_INV_RESERVE_RES = "INVENTORY_RESERVE_RES";

    private final SkuStockMapper stockMapper;
    private final SkuGlobalMapper globalMapper;
    private final StockLogMapper stockLogMapper;
    private final MessageLogMapper messageLogMapper;
    private final WarehouseMapper warehouseMapper;
    private final ObjectMapper mapper;

    @Transactional
    public void execute(Inbox inbox) {
        if (STATUS_PROCESSED.equals(inbox.getStatus())) return;

        InventoryRequestMessage msg = null;
        try {
            msg = parsePayload(inbox.getPayload());

            // 1. Phân loại xử lý dựa trên EventType
            if (OrderEventType.INVENTORY_RESERVE_REQ.name().equals(inbox.getEventType())) {
                handleReserve(msg);
                // Sau khi thành công, bắn response SUCCESS
                sendResponse(msg, true, null);
            }
            else if (OrderEventType.INVENTORY_COMPENSATE_REQ.name().equals(inbox.getEventType())) {
                handleCompensate(msg);
            }

            messageLogMapper.updateInboxStatus(inbox.getEventId(), STATUS_PROCESSED, null);
            log.info("Inventory {} processed for order: {}", inbox.getEventType(), msg.getOrderCode());

        } catch (AppException e) {
            log.error("Inventory Business Error: {}", e.getErrorCode().getMessage());
            handleGlobalError(inbox, msg, e.getErrorCode().getMessage());
        } catch (Exception e) {
            log.error("System Error in Inventory Processor: {}", e.getMessage());
            handleGlobalError(inbox, msg, "Internal Inventory Error");
            throw new RuntimeException(e);
        }
    }

    private void handleReserve(InventoryRequestMessage msg) {
        log.info(msg.toString());
        for (InventoryRequestMessage.InventoryItem item : msg.getItems()) {

            if (Boolean.TRUE.equals(item.getIsPreorder())) {
                int affected = globalMapper.incrementPreOrderCount(item.getSkuCode(), item.getQuantity());
                if (affected == 0) throw new AppException(ErrorCode.NOT_ALLOW); // Ví dụ: Hết slot đặt trước
            }
            if ("PICKUP".equals(msg.getDeliveryMethod())) {
                Warehouse warehouse = warehouseMapper.findByCode(msg.getWarehouseCode());
                var skuId=stockMapper.findByCode(item.getSkuCode(),warehouse.getId()).getSkuId();
                // Giữ chỗ tại Kho cụ thể (dành cho lấy hàng tại quầy)
                int affected = stockMapper.incrementReservedStock(skuId, warehouse.getId(), item.getQuantity());
                if (affected == 0) throw new AppException(ErrorCode.ITEM_NOT_EXISTED); // Hết hàng hoặc sai kho
                saveStockLog(msg, item, warehouse.getId(), "RESERVE_PICKUP");
            } else {
                // Giữ chỗ Global (dành cho Ship tận nơi)
                int affected = globalMapper.updateReservedQuantity(item.getSkuCode(), item.getQuantity());
                if (affected == 0) throw new AppException(ErrorCode.ITEM_NOT_EXISTED);
                saveStockLog(msg, item, null, "RESERVE_GLOBAL");
            }
        }
    }

    private void handleCompensate(InventoryRequestMessage msg) {
        for (InventoryRequestMessage.InventoryItem item : msg.getItems()) {

            try {

                // Hoàn lại slot Pre-order
                if (Boolean.TRUE.equals(item.getIsPreorder())) {
                    globalMapper.decrementPreOrderCount(item.getSkuCode(), item.getQuantity());
                }

                // Hoàn lại Stock đã Reserve
                if ("PICKUP".equals(msg.getDeliveryMethod())) {
                    Warehouse warehouse = warehouseMapper.findByCode(msg.getWarehouseCode());
                    var skuId=stockMapper.findByCode(item.getSkuCode(),warehouse.getId()).getSkuId();
                    stockMapper.decrementReservedStock(skuId, warehouse.getId(), item.getQuantity());
                    saveStockLog(msg, item, warehouse.getId(), "COMPENSATE_PICKUP");
                } else {
                    globalMapper.updateReservedQuantity(item.getSkuCode(), -item.getQuantity());
                    saveStockLog(msg, item, null, "COMPENSATE_GLOBAL");
                }
            } catch (Exception e) {
                log.error("Compensate failed for SKU: {} in Order: {}", item.getSkuCode(), msg.getOrderCode());
            }
        }
    }

    // --- HELPERS ---

    private InventoryRequestMessage parsePayload(String payload) {
        try {
            return mapper.readValue(payload, InventoryRequestMessage.class);
        } catch (Exception e) {
            throw new AppException(ErrorCode.INVALID_REQUEST_BODY);
        }
    }

    private void saveStockLog(InventoryRequestMessage msg, InventoryRequestMessage.InventoryItem item, Integer warehouseId, String type) {
        var skuId=globalMapper.findBySkuCode(item.getSkuCode()).orElseThrow(()->new AppException(ErrorCode.ITEM_NOT_EXISTED)).getSkuId();
        StockLog stockLog = StockLog.builder()
                .skuId(skuId)
                .skuCode(item.getSkuCode())
                .warehouseId(warehouseId)
                .changePhysical(0)
                .changeReserved(item.getQuantity())
                .type(type)
                .referenceId(msg.getOrderCode())
                .sagaId(msg.getSagaId())
                .note("Saga: " + (item.getIsPreorder() ? "Pre-order" : "Standard"))
                .build();
        stockLogMapper.insertLog(stockLog);
    }

    private void sendResponse(InventoryRequestMessage msg, boolean success, String errorMsg){
        InventoryResponseMessage response = InventoryResponseMessage.builder()
                .orderCode(msg.getOrderCode())
                .sagaId(msg.getSagaId())
                .success(success)
                .errorMessage(errorMsg)
                .build();

        Outbox outbox = Outbox.builder()
                .id(UUID.randomUUID().toString())
                .aggregateId(msg.getSagaId())
                .eventType(OrderEventType.INVENTORY_RESERVE_RES.name())
                .payload(mapper.writeValueAsString(response))
                .status("PENDING")
                .build();

        messageLogMapper.insertOutbox(outbox);
    }

    private void handleGlobalError(Inbox inbox, InventoryRequestMessage msg, String errorMsg) {
        messageLogMapper.updateInboxStatus(inbox.getEventId(), STATUS_FAIL, errorMsg);
        if (msg != null && OrderEventType.INVENTORY_RESERVE_REQ.name().equals(inbox.getEventType())) {
            try {
                sendResponse(msg, false, errorMsg);
            } catch (Exception e) {
                log.error("Failed to send Inventory Error Response", e);
            }
        }
    }
}