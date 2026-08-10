package com.gsvn.inventoryservice.queue.message;

import lombok.*;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResponseMessage {
    private String orderCode;
    private String sagaId;
    private boolean success;
    private String errorMessage;
}