package com.gsvn.productservice.queue;


import com.gsvn.productservice.exc.AppException;
import com.gsvn.productservice.exc.ErrorCode;
import com.gsvn.productservice.mapper.MessageLogMapper;
import com.gsvn.productservice.mapper.SkuMapper;
import com.gsvn.productservice.model.dto.SkuCartDetailsDTO;
import com.gsvn.productservice.model.entity.Inbox;
import com.gsvn.productservice.model.entity.Outbox;
import com.gsvn.productservice.model.saga.OrderEventType;
import com.gsvn.productservice.queue.message.SkuValidateRequestMessage;
import com.gsvn.productservice.queue.message.SkuValidateResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductInboxProcessor {

    private static final String STATUS_PROCESSED = "PROCESSED";
    private static final String STATUS_FAIL = "FAIL";
    private static final String STATUS_PENDING = "PENDING";

    private final SkuMapper skuMapper;
    private final MessageLogMapper logMapper;
    private final ObjectMapper objectMapper;

    @Transactional
    public void execute(Inbox inbox) {
        if (STATUS_PROCESSED.equals(inbox.getStatus())) return;

        SkuValidateRequestMessage request = null;
        try {
            request = parsePayload(inbox.getPayload());
            validateAndSendResponse(request);

            logMapper.updateInboxStatus(inbox.getEventId(), STATUS_PROCESSED, null);
            log.info("Successfully validated SKUs for Order: {}", request.getOrderCode());

        } catch (AppException e) {
            log.error("Validation Business Error: {}", e.getErrorCode().getMessage());
            handleGlobalError(inbox, request, e.getErrorCode().getMessage());
        } catch (Exception e) {
            log.error("System Error in Product Processor: {}", e.getMessage());
            handleGlobalError(inbox, request, "Internal System Error");
            throw new RuntimeException(e);
        }
    }

    private void validateAndSendResponse(SkuValidateRequestMessage request){
        List<String> skuIds = request.getItems().stream()
                .map(SkuValidateRequestMessage.SkuRequestItem::getSkuCode)
                .collect(Collectors.toList());

        List<SkuCartDetailsDTO> dbDetails = skuMapper.findCartDetailsBySkuCodes(skuIds);
        Map<String, SkuCartDetailsDTO> dbMap = dbDetails.stream()
                .collect(Collectors.toMap(SkuCartDetailsDTO::getSkuCode, d -> d));

        List<SkuValidateResponseMessage.SkuResponseItem> responseItems = new ArrayList<>();
        boolean hasError = false;
        String errorMessage = null;

        for (SkuValidateRequestMessage.SkuRequestItem itemReq : request.getItems()) {
            SkuCartDetailsDTO detail = dbMap.get(itemReq.getSkuCode());

            if (detail == null || !Boolean.TRUE.equals(detail.getIsSkuActive())
                    || !Boolean.TRUE.equals(detail.getIsProductActive())) {
                hasError = true;
                errorMessage = "PRODUCT_OR_SKU_INACTIVE_OR_NOT_FOUND: " + itemReq.getSkuCode();
                break;
            }

            if (Boolean.TRUE.equals(itemReq.getIsPreorder()) && !Boolean.TRUE.equals(detail.getIsPreOrder())) {
                hasError = true;
                errorMessage = "PREORDER_NOT_AVAILABLE_FOR_SKU: " + itemReq.getSkuCode();
                break;
            }

            responseItems.add(mapToResponseItem(detail, itemReq));
        }

        sendResponse(request, responseItems, hasError, errorMessage);
    }

    private SkuValidateResponseMessage.SkuResponseItem mapToResponseItem(SkuCartDetailsDTO detail, SkuValidateRequestMessage.SkuRequestItem itemReq) {
        return SkuValidateResponseMessage.SkuResponseItem.builder()
                .productId(detail.getProductId())
                .productName(detail.getProductName())
                .skuId(detail.getSkuId())
                .skuCode(detail.getSkuCode())
                .imageUrl(detail.getImageUrl())
                .sellingPrice(detail.getSellingPrice())
                .prePrice(detail.getPrePrice())
                .preDepositAmount(detail.getPreDepositAmount())
                .isPreorderItem(detail.getIsPreOrder())
                .quantity(itemReq.getQuantity())
                .preReleaseDate(detail.getPreReleaseDate())
                .build();
    }

    private void sendResponse(SkuValidateRequestMessage req, List<SkuValidateResponseMessage.SkuResponseItem> items, boolean hasError, String errorMsg){
        SkuValidateResponseMessage res = SkuValidateResponseMessage.builder()
                .orderCode(req.getOrderCode())
                .sagaId(req.getSagaId())
                .hasError(hasError)
                .errorMessage(errorMsg)
                .items(hasError ? null : items)
                .build();

        Outbox outbox = Outbox.builder()
                .id(UUID.randomUUID().toString())
                .aggregateId(req.getSagaId())
                .eventType(OrderEventType.SKU_VALIDATE_RES.name())
                .payload(objectMapper.writeValueAsString(res))
                .status(STATUS_PENDING)
                .build();

        logMapper.insertOutbox(outbox);
    }

    private SkuValidateRequestMessage parsePayload(String payload) {
        try {
            return objectMapper.readValue(payload, SkuValidateRequestMessage.class);
        } catch (Exception e) {
            throw new AppException(ErrorCode.INVALID_REQUEST_BODY);
        }
    }

    private void handleGlobalError(Inbox inbox, SkuValidateRequestMessage request, String errorMsg) {
        logMapper.updateInboxStatus(inbox.getEventId(), STATUS_FAIL, errorMsg);
        if (request != null) {
            try {
                sendResponse(request, null, true, errorMsg);
            } catch (Exception e) {
                log.error("Failed to send Error Response Outbox", e);
            }
        }
    }
}