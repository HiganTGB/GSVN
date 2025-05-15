package com.tgb.gsvnbackend.queue.consumer;

import com.tgb.gsvnbackend.queue.message.ItemReleaseMessage;
import com.tgb.gsvnbackend.queue.message.ItemReverseMessage;
import com.tgb.gsvnbackend.service.InventoryService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InventoryConsumer {
    private final InventoryService inventoryService;
    @Autowired
    public InventoryConsumer(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }
    @RabbitListener(queues = "inventory-reverse-queue" ,concurrency = "1")
    public void receiverInventoryReverse(ItemReverseMessage message)
    {

            inventoryService.decreaseStock(message.orderId(),message.lineItems());
    }
    @RabbitListener(queues = "inventory-release-queue" ,concurrency = "1")
    public void receiverInventoryRelease(ItemReleaseMessage message)
    {

        inventoryService.increaseStock(message.orderId(),message.lineItems());
    }
}
