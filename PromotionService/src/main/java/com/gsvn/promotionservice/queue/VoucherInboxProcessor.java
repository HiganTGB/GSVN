package com.gsvn.promotionservice.queue;


import com.gsvn.promotionservice.exc.AppException;
import com.gsvn.promotionservice.exc.ErrorCode;
import com.gsvn.promotionservice.mapper.MessageLogMapper;
import com.gsvn.promotionservice.mapper.VoucherMapper;
import com.gsvn.promotionservice.mapper.VoucherUsageHistoryMapper;
import com.gsvn.promotionservice.model.entity.Inbox;
import com.gsvn.promotionservice.model.entity.Outbox;
import com.gsvn.promotionservice.model.entity.Voucher;
import com.gsvn.promotionservice.model.entity.VoucherUsageHistory;
import com.gsvn.promotionservice.model.saga.OrderEventType;
import com.gsvn.promotionservice.queue.message.VoucherRequestMessage;
import com.gsvn.promotionservice.queue.message.VoucherResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class VoucherInboxProcessor {

    private static final String STATUS_PROCESSED = "PROCESSED";
    private static final String STATUS_FAIL = "FAIL";
    private static final String EVENT_VOUCHER_RES = "VOUCHER_APPLY_RES";

    private final VoucherMapper voucherMapper;
    private final VoucherUsageHistoryMapper usageHistoryMapper;
    private final MessageLogMapper logMapper;
    private final ObjectMapper mapper;

    @Transactional
    public void execute(Inbox inbox) {
        if (STATUS_PROCESSED.equals(inbox.getStatus())) return;

        VoucherRequestMessage request = null;
        try {
            request = parsePayload(inbox.getPayload());

            // Phân loại luồng xử lý
            if (OrderEventType.VOUCHER_APPLY_REQ.name().equals(inbox.getEventType())) {
                handleApplyVoucher(request);
            }
            else if (OrderEventType.VOUCHER_COMPENSATE_REQ.name().equals(inbox.getEventType())) {
                handleCompensateVoucher(request);
            }

            logMapper.updateInboxStatus(inbox.getEventId(), STATUS_PROCESSED, null);

        } catch (AppException e) {
            log.error("Voucher Business Error: {}", e.getErrorCode().getMessage());
            handleGlobalError(inbox, request, e.getErrorCode().getMessage());
        } catch (Exception e) {
            log.error("System Error in Voucher Processor: {}", e.getMessage());
            handleGlobalError(inbox, request, "Internal Promotion Error");
            throw new RuntimeException(e);
        }
    }

    private void handleApplyVoucher(VoucherRequestMessage request) {
        Voucher voucher = voucherMapper.findByCode(request.getVoucherCode());

        // 1. Kiểm tra tính hợp lệ
        validateVoucher(voucher, request);

        // 2. Tính số tiền giảm
        BigDecimal discountAmount = calculateDiscount(voucher, request.getTotalAmount());

        // 3. Cập nhật số lượt dùng (Dùng Optimistic Locking với version)
        int updatedRows = voucherMapper.incrementUsedCount(voucher.getId(), voucher.getVersion());
        if (updatedRows == 0) {
            throw new AppException(ErrorCode.NOT_ALLOW); // Voucher vừa hết lượt hoặc bị tranh chấp
        }

        // 4. Lưu lịch sử sử dụng
        saveUsageHistory(voucher.getId(), request);

        // 5. Phản hồi thành công
        sendResponse(request, true, discountAmount, null);
    }

    private void handleCompensateVoucher(VoucherRequestMessage req) {
        log.info("Compensating voucher for order: {}", req.getOrderCode());
        Voucher voucher = voucherMapper.findByCode(req.getVoucherCode());
        // Hoàn lại lượt dùng
        voucherMapper.decrementUsedCount(voucher.getId());

        // Xóa lịch sử sử dụng để user có thể dùng lại mã này cho đơn khác
        usageHistoryMapper.deleteBySagaId(req.getSagaId());

        log.info("Voucher compensated successfully for Order: {}", req.getOrderCode());
    }

    // --- LOGIC TRỢ GIÚP ---

    private void validateVoucher(Voucher v, VoucherRequestMessage req) {
        if (v == null || !Boolean.TRUE.equals(v.getIsActive()))
            throw new AppException(ErrorCode.ITEM_NOT_EXISTED);

        OffsetDateTime now = OffsetDateTime.now();
        if (now.isBefore(v.getStartDate()) || now.isAfter(v.getEndDate()))
            throw new AppException(ErrorCode.NOT_ALLOW); // Hết hạn

        if (req.getTotalAmount().compareTo(v.getMinOrderValue()) < 0)
            throw new AppException(ErrorCode.INVALID_REQUEST_BODY); // Không đủ min order

        if (v.getUsedCount() >= v.getUsageLimit())
            throw new AppException(ErrorCode.NOT_ALLOW); // Hết lượt dùng hệ thống

        int userUsed = usageHistoryMapper.countUserUsage(v.getId(), req.getCustomerId(), req.getGuestEmail());
        if (userUsed >= v.getLimitPer())
            throw new AppException(ErrorCode.NOT_ALLOW); // User hết lượt dùng mã này
    }

    private BigDecimal calculateDiscount(Voucher v, BigDecimal totalAmount) {
        BigDecimal discount;
        if ("PERCENTAGE".equals(v.getDiscountType())) {
            discount = totalAmount.multiply(v.getDiscountValue()).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            if (v.getMaxDiscountAmount() != null && discount.compareTo(v.getMaxDiscountAmount()) > 0) {
                discount = v.getMaxDiscountAmount();
            }
        } else {
            discount = v.getDiscountValue();
        }
        return discount.min(totalAmount);
    }

    private VoucherRequestMessage parsePayload(String payload) {
        try {
            return mapper.readValue(payload, VoucherRequestMessage.class);
        } catch (Exception e) {
            throw new AppException(ErrorCode.INVALID_REQUEST_BODY);
        }
    }

    private void saveUsageHistory(Integer voucherId, VoucherRequestMessage req) {
        VoucherUsageHistory history = VoucherUsageHistory.builder()
                .voucherId(voucherId)
                .customerId(req.getCustomerId())
                .guestEmail(req.getGuestEmail())
                .orderId(req.getOrderId())
                .sagaId(req.getSagaId())
                .usedAt(OffsetDateTime.now())
                .build();
        usageHistoryMapper.insert(history);
    }

    private void sendResponse(VoucherRequestMessage req, boolean success, BigDecimal discount, String errorMsg){
        VoucherResponseMessage response = VoucherResponseMessage.builder()
                .orderCode(req.getOrderCode())
                .sagaId(req.getSagaId())
                .success(success)
                .discountAmount(discount)
                .errorMessage(errorMsg)
                .build();

        Outbox outbox = Outbox.builder()
                .id(UUID.randomUUID().toString())
                .aggregateId(req.getSagaId())
                .eventType(EVENT_VOUCHER_RES)
                .payload(mapper.writeValueAsString(response))
                .status("PENDING")
                .build();

        logMapper.insertOutbox(outbox);
    }

    private void handleGlobalError(Inbox inbox, VoucherRequestMessage request, String errorMsg) {
        logMapper.updateInboxStatus(inbox.getEventId(), STATUS_FAIL, errorMsg);
        if (request != null && "VOUCHER_APPLY_REQ".equals(inbox.getEventType())) {
            try {
                sendResponse(request, false, BigDecimal.ZERO, errorMsg);
            } catch (Exception e) {
                log.error("Failed to send Voucher Error Response", e);
            }
        }
    }
}