package com.gsvn.paymentservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VNPayResponse {
    private String RspCode;
    private String Message;
}