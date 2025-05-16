package com.tgb.gsvnbackend.controller;

import com.tgb.gsvnbackend.config.VNPAYConfig;
import com.tgb.gsvnbackend.lib.VNPayUtils;
import com.tgb.gsvnbackend.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService paymentService;
    private final VNPAYConfig vnPayConfig;
    @Autowired
    public PaymentController(PaymentService paymentService, VNPAYConfig vnpayConfig) {
        this.paymentService = paymentService;
        this.vnPayConfig = vnpayConfig;
    }
    @GetMapping("/vnpay-return")
    public ResponseEntity<?> vnpayReturn(@RequestParam Map<String, String> queryParams, HttpServletRequest request) {
        String vnpSecureHash = queryParams.get("vnp_SecureHash");

        if (vnpSecureHash == null || vnpSecureHash.isEmpty()) {
            System.err.println("Invalid VNPay callback: Missing vnp_SecureHash");
            return new ResponseEntity<>("Invalid signature", HttpStatus.BAD_REQUEST);
        }

        Map<String, String> paramsToCheck = new HashMap<>(queryParams);
        paramsToCheck.remove("vnp_SecureHash");
        paramsToCheck.remove("vnp_SecureHashType");

        if (!VNPayUtils.isValidSignature(paramsToCheck, vnpSecureHash, vnPayConfig.getSecretKey())) {
            System.err.println("Invalid VNPay callback: Signature mismatch");
            return new ResponseEntity<>("Invalid signature", HttpStatus.BAD_REQUEST);
        }

        String vnpTxnRef = queryParams.get("vnp_TxnRef");
        String vnpTransactionStatus = queryParams.get("vnp_TransactionStatus");
        String vnpOrderInfo = queryParams.get("vnp_OrderInfo");
        String vnpPayDate = queryParams.get("vnp_PayDate");
        String vnpTransactionNo = queryParams.get("vnp_TransactionNo");
        String vnpAmount = queryParams.get("vnp_Amount");
        String vnpBankCode = queryParams.get("vnp_BankCode");
        String vnpCardType = queryParams.get("vnp_CardType");

        if ("00".equals(vnpTransactionStatus)) {
            paymentService.paidVNPAYResult(Integer.parseInt(vnpTxnRef), vnpTransactionNo, true);
            return ResponseEntity.ok("Giao dịch thành công");
        } else {
            paymentService.paidVNPAYResult(Integer.parseInt(vnpTxnRef), vnpTransactionNo, false);

            return ResponseEntity.badRequest().body("Giao dịch thất bại: " + queryParams.get("vnp_Message"));
        }
    }
    @PostMapping("/cod/{paymentId}/confirm")
    public ResponseEntity<Void> confirmCodPayment(
            @PathVariable int paymentId,
            Principal user
    ) {
        paymentService.paidCOD(paymentId, user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
