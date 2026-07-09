package com.gsvn.inventoryservice.service;

import com.gsvn.inventoryservice.client.SkuSearchInternalClient;
import com.gsvn.inventoryservice.client.StaffServiceFeignClient;
import com.gsvn.inventoryservice.common.ApiResponse;
import com.gsvn.inventoryservice.common.PageResponse;
import com.gsvn.inventoryservice.converter.OutboundConverter;
import com.gsvn.inventoryservice.exc.AppException;
import com.gsvn.inventoryservice.exc.ErrorCode;
import com.gsvn.inventoryservice.mapper.*;
import com.gsvn.inventoryservice.model.internal.SkuSearchResponse;
import com.gsvn.inventoryservice.model.dto.request.OutboundRequest;
import com.gsvn.inventoryservice.model.dto.response.OutboundResponse;
import com.gsvn.inventoryservice.model.entity.OutboundItem;
import com.gsvn.inventoryservice.model.entity.OutboundReceipt;
import com.gsvn.inventoryservice.model.entity.StockLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OutboundService {

    private final OutboundMapper outboundMapper;
    private final SkuStockMapper skuStockMapper;
    private final StockLogMapper stockLogMapper;
    private final OutboundConverter outboundConverter;
    private final AuthenticationService authenticationService;
    private final SkuSearchInternalClient skuSearchInternalClient;
    private final StaffServiceFeignClient staffServiceFeignClient;
    private final PdfExportService pdfExportService;
    private final WarehouseMapper warehouseMapper;
    @Transactional(rollbackFor = Exception.class)
    public OutboundResponse processOutbound(OutboundRequest request) {
        var staffId = authenticationService.getStaffIdFromToken();

        List<Long> skuIds = request.getItems().stream()
                .map(OutboundRequest.OutboundItemRequest::getSkuId)
                .toList();

        Map<Long, SkuSearchResponse> skuMap = validateAndGetSkuInfo(skuIds);

        OutboundReceipt receipt = outboundConverter.toEntity(request);
        receipt.setStaffId(staffId);
        outboundMapper.insertReceipt(receipt);

        List<OutboundItem> items = request.getItems().stream().map(itemReq -> {
            SkuSearchResponse skuInfo = skuMap.get(itemReq.getSkuId());

            int rowsAffected = skuStockMapper.deductPhysicalStock(
                    itemReq.getSkuId(),
                    request.getWarehouseId(),
                    itemReq.getQuantity()
            );

            if (rowsAffected == 0) {
                throw new AppException(ErrorCode.INSUFFICIENT_STOCK);
            }

            stockLogMapper.insertLog(StockLog.builder()
                    .skuId(itemReq.getSkuId())
                    .skuCode(skuInfo.getSkuCode())
                    .warehouseId(request.getWarehouseId())
                    .changePhysical(-itemReq.getQuantity())
                    .type("OUTBOUND_" + request.getType())
                    .referenceId(receipt.getReceiptCode())
                    .staffId(staffId)
                    .build());

            return OutboundItem.builder()
                    .outboundId(receipt.getId())
                    .skuId(itemReq.getSkuId())
                    .skuCode(skuInfo.getSkuCode())
                    .productName(skuInfo.getProductName())
                    .quantity(itemReq.getQuantity())
                    .build();
        }).collect(Collectors.toList());

        outboundMapper.insertBatchItems(items);

        return outboundConverter.toResponse(receipt, items);
    }

    public PageResponse<OutboundResponse> getOutboundPage(Integer warehouseId, String type, String keyword, int page, int size) {
        int offset = (page - 1) * size;
        List<OutboundReceipt> receipts = outboundMapper.findOutboundPage(warehouseId, type, keyword, size, offset);
        long total = outboundMapper.countOutbound(warehouseId, type, keyword);

        List<OutboundResponse> responseList = receipts.stream()
                .map(r -> outboundConverter.toResponse(r, null))
                .toList();

        return PageResponse.of(responseList, total, page, size);
    }

    public OutboundResponse getOutboundDetail(Long id) {
        OutboundReceipt receipt = outboundMapper.findById(id);
        if (receipt == null) throw new AppException(ErrorCode.ITEM_NOT_EXISTED);

        List<OutboundItem> items = outboundMapper.findItemsByOutboundId(id);
        return outboundConverter.toResponse(receipt, items);
    }

    private Map<Long, SkuSearchResponse> validateAndGetSkuInfo(List<Long> skuIds) {
        ApiResponse<Map<Long, SkuSearchResponse>> response = skuSearchInternalClient.getByIds(skuIds);

        if (response == null || response.result() == null || response.result().size() != skuIds.size()) {
            throw new AppException(ErrorCode.SKU_NOT_FOUND);
        }

        return response.result();
    }
    public byte[] exportOutboundDetail(Long id) {
        OutboundReceipt receipt = outboundMapper.findById(id);
        if (receipt == null) throw new AppException(ErrorCode.ITEM_NOT_EXISTED);

        List<OutboundItem> items = outboundMapper.findItemsByOutboundId(id);
        OutboundResponse outbound = outboundConverter.toResponse(receipt, items);

        String staffName=staffServiceFeignClient.getInternalById(receipt.getStaffId()).result().getFullName();
        String warehouseName=warehouseMapper.findById(receipt.getWarehouseId()).getName();

        return pdfExportService.exportOutboundPdf(outbound, staffName, warehouseName);
    }
}