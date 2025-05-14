package com.tgb.gsvnbackend.queue.config;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InventoryMQConfig {
    // Inventory Reverse
    @Bean
    public Queue inventoryReverseConsumerQueue() {
        return QueueBuilder.durable("inventory-reverse-queue").build();
    }

    @Bean
    public Binding inventoryReverseBinding(Queue inventoryReverseConsumerQueue, Exchange exchange) {
        return BindingBuilder.bind(inventoryReverseConsumerQueue).to(exchange).with("inventory.reverse").noargs();
    }
    // Inventory Release
    @Bean
    public Queue inventoryReleaseConsumerQueue() {
        return QueueBuilder.durable("inventory-release-queue").build();
    }

    @Bean
    public Binding  inventoryReleaseQueue(Queue inventoryReleaseConsumerQueue, Exchange exchange) {
        return BindingBuilder.bind(inventoryReleaseConsumerQueue).to(exchange).with("inventory.release").noargs();
    }
}
