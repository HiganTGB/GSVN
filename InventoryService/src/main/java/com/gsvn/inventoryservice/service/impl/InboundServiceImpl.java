package com.gsvn.inventoryservice.service.impl;

import com.gsvn.inventoryservice.client.SkuSearchInternalClient;
import com.gsvn.inventoryservice.client.StaffServiceFeignClient;
import com.gsvn.inventoryservice.common.ApiResponse;
import com.gsvn.inventoryservice.common.PageResponse;
import com.gsvn.inventoryservice.converter.InboundConverter;
import com.gsvn.inventoryservice.exc.AppException;
import com.gsvn.inventoryservice.exc.ErrorCode;
import com.gsvn.inventoryservice.mapper.*;
import com.gsvn.inventoryservice.model.entity.Supplier;
import com.gsvn.inventoryservice.model.internal.SkuSearchResponse;
import com.gsvn.inventoryservice.model.dto.request.InboundRequest;
import com.gsvn.inventoryservice.model.dto.response.InboundResponse;
import com.gsvn.inventoryservice.model.entity.InboundItem;
import com.gsvn.inventoryservice.model.entity.InboundReceipt;
import com.gsvn.inventoryservice.model.entity.StockLog;
import com.gsvn.inventoryservice.service.InboundService;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InboundServiceImpl implements InboundService {
    private final SpringTemplateEngine templateEngine;
    private final InboundMapper inboundMapper;
    private final SkuStockMapper skuStockMapper;
    private final StockLogMapper stockLogMapper;
    private final InboundConverter inboundConverter;
    private final AuthenticationServiceImpl authenticationService;
    private final SkuSearchInternalClient skuSearchInternalClient;
    private final SupplierMapper supplierMapper;
    private final StaffServiceFeignClient staffServiceFeignClient;
    private final WarehouseMapper warehouseMapper;

    @Transactional(rollbackFor = Exception.class)
    public InboundResponse processInbound(InboundRequest request) {
        var staffId = authenticationService.getStaffIdFromToken();

        List<Long> skuIds = request.getItems().stream()
                .map(InboundRequest.InboundItemRequest::getSkuId)
                .toList();


        Map<Long, SkuSearchResponse> skuMap = validateAndGetSkuInfo(skuIds);

        InboundReceipt receipt = inboundConverter.toEntity(request);
        receipt.setStaffId(staffId);
        inboundMapper.insertReceipt(receipt);

        List<InboundItem> items = request.getItems().stream().map(itemReq -> {

            SkuSearchResponse skuInfo = skuMap.get(itemReq.getSkuId());

            skuStockMapper.upsertPhysicalStock(
                    itemReq.getSkuId(),
                    skuInfo.getSkuCode(),
                    request.getWarehouseId(),
                    itemReq.getQuantity()
            );

            stockLogMapper.insertLog(StockLog.builder()
                    .skuId(itemReq.getSkuId())
                    .skuCode(skuInfo.getSkuCode())
                    .warehouseId(request.getWarehouseId())
                    .changePhysical(itemReq.getQuantity())
                    .type("INBOUND")
                    .referenceId(receipt.getReceiptCode())
                    .staffId(staffId)
                    .build());

            return InboundItem.builder()
                    .inboundId(receipt.getId())
                    .skuId(itemReq.getSkuId())
                    .skuCode(skuInfo.getSkuCode())
                    .productName(skuInfo.getProductName())
                    .quantity(itemReq.getQuantity())
                    .importPrice(itemReq.getImportPrice())
                    .build();
        }).collect(Collectors.toList());

        inboundMapper.insertBatchItems(items);

        return inboundConverter.toResponse(receipt, items);
    }


    private Map<Long, SkuSearchResponse> validateAndGetSkuInfo(List<Long> skuIds) {
        ApiResponse<Map<Long, SkuSearchResponse>> response = skuSearchInternalClient.getByIds(skuIds);

        if (response == null || response.result() == null || response.result().size() != skuIds.size()) {
            throw new AppException(ErrorCode.SKU_NOT_FOUND);
        }

        return response.result();
    }

    public PageResponse<InboundResponse> getInboundPage(Integer warehouseId,Integer supplierId, String type, String keyword, int page, int size) {
        int offset = (page - 1) * size;
        List<InboundReceipt> receipts = inboundMapper.findInboundPage(warehouseId,supplierId, type, keyword, size, offset);
        long total = inboundMapper.countInbound(warehouseId, supplierId, type, keyword);

        List<InboundResponse> responseList = receipts.stream()
                .map(r -> inboundConverter.toResponse(r, null))
                .toList();

        return PageResponse.of(responseList,total,page,size);

    }

    public InboundResponse getInboundDetail(Long id) {
        InboundReceipt receipt = inboundMapper.findById(id);
        if (receipt == null) throw new AppException(ErrorCode.ITEM_NOT_EXISTED);

        List<InboundItem> items = inboundMapper.findItemsByInboundId(id);
        return  inboundConverter.toResponse(receipt, items);
    }
    public byte[] exportInboundDetail(Long id) {
        InboundReceipt receipt = inboundMapper.findById(id);
        if (receipt == null) throw new AppException(ErrorCode.ITEM_NOT_EXISTED);

        List<InboundItem> items = inboundMapper.findItemsByInboundId(id);
        var inbound= inboundConverter.toResponse(receipt, items);
        String staffName=staffServiceFeignClient.getInternalById(receipt.getStaffId()).result().getFullName();
        String warehouseName=warehouseMapper.findById(receipt.getWarehouseId()).getName();
        var supplier=supplierMapper.findById(receipt.getSupplierId()).orElse(Supplier.builder().name("Unknown").build());
        String supplierName=supplier.getName();
        return exportInboundPdf(inbound,supplierName,staffName,warehouseName);
    }
    private byte[] exportInboundPdf(InboundResponse data, String supplierName, String staffName, String warehouseName) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream();
             java.io.InputStream fontStream = new ClassPathResource("/fonts/times.ttf").getInputStream()) {

            Context context = new Context();
            context.setVariable("receipt", data);
            context.setVariable("warehouseName", warehouseName);
            context.setVariable("supplierName", supplierName);
            context.setVariable("staffName", staffName);
            String htmlContent = templateEngine.process("pdf/inbound", context);

            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();

            builder.useFont(() -> fontStream, "Times New Roman");

            builder.withHtmlContent(htmlContent, "/");
            builder.toStream(os);
            builder.run();

            return os.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error render PDF: " + e.getMessage());
        }
    }




}