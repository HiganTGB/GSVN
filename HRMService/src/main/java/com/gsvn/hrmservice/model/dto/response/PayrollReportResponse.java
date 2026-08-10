package com.gsvn.hrmservice.model.dto.response;

import com.gsvn.hrmservice.model.dto.PayrollReportItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PayrollReportResponse {
    private String reportPeriod;
    private List<PayrollReportItem> items;
    private PayrollReportItem summary;
}