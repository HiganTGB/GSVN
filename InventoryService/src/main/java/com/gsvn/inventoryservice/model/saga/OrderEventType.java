package com.gsvn.inventoryservice.model.saga;

public enum OrderEventType {
    // Phase 1: Product SKU Validation
    SKU_VALIDATE_REQ,
    SKU_VALIDATE_RES,

    // Phase 2: Inventory Reservation
    INVENTORY_RESERVE_REQ,
    INVENTORY_RESERVE_RES,

    // Phase 3: Voucher Application
    VOUCHER_APPLY_REQ,
    VOUCHER_APPLY_RES,

    // Phase 4: Payment URL Generation
    PAYMENT_URL_REQ,
    PAYMENT_URL_RES,

    // Phase 5: Payment Result (Xử lý sau khi khách trả tiền xong - IPN)
    PAYMENT_COMPLETED_EVENT,
    PAYMENT_FAILED_EVENT,


    INVENTORY_COMPENSATE_REQ,
    VOUCHER_COMPENSATE_REQ,


    SEND_NOTIFICATION,
    ORDER_COMPLETED_EVENT,
    ORDER_CANCELLED_EVENT
}