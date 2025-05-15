package com.tgb.gsvnbackend.service.impl;

import com.tgb.gsvnbackend.exc.DataViolationException;
import com.tgb.gsvnbackend.exc.NotFoundException;
import com.tgb.gsvnbackend.model.domain.LineItemDomain;
import com.tgb.gsvnbackend.model.entity.SKU;
import com.tgb.gsvnbackend.model.enumeration.Type;
import com.tgb.gsvnbackend.queue.producer.InventoryProducer;
import com.tgb.gsvnbackend.repository.jpaRepository.SKURepository;
import com.tgb.gsvnbackend.service.InventoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class InventoryServiceImp implements InventoryService {
    private final SKURepository skuRepository;
    private final InventoryProducer inventoryProducer;
    @Autowired
    public InventoryServiceImp(SKURepository skuRepository, InventoryProducer inventoryProducer) {
        this.skuRepository = skuRepository;
        this.inventoryProducer = inventoryProducer;
        log.info("InventoryServiceImp initialized.");
    }
    public  void  decreaseStock(String orderId,List<LineItemDomain> lineItems)
    {
        log.info("Attempting to decrease stock for order ID: {}", orderId);
        try {
            List<LineItemDomain> itemInfoList=new ArrayList<>();
            for (LineItemDomain item : lineItems) {
                String skuId = item.sku();
                Integer quantity = item.quantity();
                log.debug("Processing SKU ID: {}, Quantity: {}", skuId, quantity);
                SKU sku = skuRepository.findById(Integer.valueOf(skuId))
                        .orElseThrow(() -> {
                            log.error("SKU not found: {}", skuId);
                            return new NotFoundException("SKU not found: " + skuId);
                        });
                if(sku.getType()== Type.Rumor) {
                    log.warn("Attempted to decrease stock for rumor SKU: {}", skuId);
                    throw new DataViolationException("Sku is rumor :" +skuId);
                }
                if(sku.getType()==Type.PreOrder&&(sku.getStartOrder().after(new Date())||sku.getEndOrder().before(new Date())))
                {
                    log.warn("Attempted to decrease stock for pre-order SKU outside order period: {}", skuId);
                    throw new DataViolationException("Sku is end order :" +skuId);
                }
                if (sku.getStock() >= quantity) {
                    sku.setStock(sku.getStock() - quantity);
                    SKU saveSKU= skuRepository.save(sku);
                    itemInfoList.add(
                            new LineItemDomain(String.valueOf(saveSKU.getSkuId()),saveSKU.getNo(),saveSKU.getTitle(),quantity,saveSKU.getPrice(),saveSKU.getPrice())
                    );
                    log.info("Successfully decreased stock for SKU ID: {}, new stock: {}", skuId, saveSKU.getStock());

                } else {
                    log.warn("Insufficient stock for SKU: {}. Available: {}, Requested: {}", skuId, sku.getStock(), quantity);
                    throw new DataViolationException(String.format("Insufficient stock for SKU: %s.  Available: %d, Requested: %d",
                            skuId, sku.getStock(), quantity));
                }
            }
            inventoryProducer.sendItemReversedSuccess(orderId,itemInfoList);
            log.info("Successfully processed stock decrease for order ID: {}, sending success message.", orderId);

        }catch (DataViolationException | NotFoundException e)
        {
            log.error("Failed to decrease stock for order ID: {}. Reason: {}", orderId, e.getMessage());
            inventoryProducer.sendItemReversedFail(orderId);
            log.info("Sending failure message for stock decrease reversal for order ID: {}", orderId);
        }

    }
    public  void  increaseStock(String orderId,List<LineItemDomain> lineItems)
    {
        log.info("Attempting to increase stock for order ID: {}", orderId);
        for (LineItemDomain item : lineItems) {
            String skuId = item.sku();
            Integer quantity = item.quantity();
            log.debug("Processing SKU ID: {}, Quantity: {}", skuId, quantity);
            skuRepository.findById(Integer.valueOf(skuId))
                    .ifPresent(
                            sku->{
                                int oldStock = sku.getStock();
                                sku.setStock(oldStock + quantity);
                                SKU saveSKU= skuRepository.save(sku);
                                log.info("Successfully increased stock for SKU ID: {}, old stock: {}, new stock: {}", skuId, oldStock, saveSKU.getStock());
                            }
                    );

        }
        log.info("Successfully processed stock increase for order ID: {}", orderId);

    }
}