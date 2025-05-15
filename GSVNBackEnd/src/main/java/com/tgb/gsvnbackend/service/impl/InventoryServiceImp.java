package com.tgb.gsvnbackend.service.impl;

import com.tgb.gsvnbackend.exc.DataViolationException;
import com.tgb.gsvnbackend.exc.NotFoundException;
import com.tgb.gsvnbackend.model.domain.LineItemDomain;
import com.tgb.gsvnbackend.model.entity.SKU;
import com.tgb.gsvnbackend.model.enumeration.Type;
import com.tgb.gsvnbackend.queue.producer.InventoryProducer;
import com.tgb.gsvnbackend.repository.jpaRepository.SKURepository;
import com.tgb.gsvnbackend.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class InventoryServiceImp implements InventoryService {
    private final SKURepository skuRepository;
    private final InventoryProducer inventoryProducer;
    @Autowired
    public InventoryServiceImp(SKURepository skuRepository, InventoryProducer inventoryProducer) {
        this.skuRepository = skuRepository;
        this.inventoryProducer = inventoryProducer;
    }
    public  void  decreaseStock(String orderId,List<LineItemDomain> lineItems)
    {
        try {
        List<LineItemDomain> itemInfoList=new ArrayList<>();
        for (LineItemDomain item : lineItems) {
            String skuId = item.sku();
            Integer quantity = item.quantity();
            SKU sku = skuRepository.findById(Integer.valueOf(skuId))
                    .orElseThrow(() -> new NotFoundException("SKU not found: " + skuId));
            if(sku.getType()== Type.Rumor) throw new DataViolationException("Sku is rumor :" +skuId);
            if(sku.getType()==Type.PreOrder&&(sku.getStartOrder().after(new Date())||sku.getEndOrder().before(new Date())))
            {
                throw new DataViolationException("Sku is end order :" +skuId);
            }
            if (sku.getStock() >= quantity) {
                sku.setStock(sku.getStock() - quantity);
                SKU saveSKU= skuRepository.save(sku);
                itemInfoList.add(
                        new LineItemDomain(String.valueOf(saveSKU.getSkuId()),saveSKU.getNo(),saveSKU.getTitle(),quantity,saveSKU.getPrice(),saveSKU.getPrice())
                );

            } else {
                throw new DataViolationException(String.format("Insufficient stock for SKU: %s.  Available: %d, Requested: %d",
                        skuId, sku.getStock(), quantity));
            }
        }
            inventoryProducer.sendItemReversedSuccess(orderId,itemInfoList);

        }catch (DataViolationException | NotFoundException e)
        {
            inventoryProducer.sendItemReversedFail(orderId);
        }

    }
    public  void  increaseStock(String orderId,List<LineItemDomain> lineItems)
    {

        for (LineItemDomain item : lineItems) {
            String skuId = item.sku();
            Integer quantity = item.quantity();
            skuRepository.findById(Integer.valueOf(skuId))
                    .ifPresent(
                            sku->{
                                sku.setStock(sku.getStock() + quantity);
                                SKU saveSKU= skuRepository.save(sku);
                            }
                    );

        }

    }
}
