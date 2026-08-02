package com.gsvn.inventoryservice.controller;

import com.gsvn.inventoryservice.common.ApiResponse;
import com.gsvn.inventoryservice.common.PageResponse;
import com.gsvn.inventoryservice.model.dto.request.OutboundRequest;
import com.gsvn.inventoryservice.model.dto.response.OutboundResponse;
import com.gsvn.inventoryservice.service.OutboundService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/outbound")
@RequiredArgsConstructor
public class OutboundController {

    private final OutboundService outboundService;

    @PostMapping
    @PreAuthorize("hasAuthority('all') or hasAuthority('outbound_create')")
    public ApiResponse<OutboundResponse> createOutbound(@RequestBody @Valid OutboundRequest request) {
        return new ApiResponse<>(outboundService.processOutbound(request));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('all') or hasAuthority('stock_read')")
    public ApiResponse<PageResponse<OutboundResponse>> getOutboundPage(
            @RequestParam(required = false) Integer warehouseId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return new ApiResponse<>(outboundService.getOutboundPage(warehouseId, type, keyword, page, size));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('all') or hasAuthority('stock_read')")
    public ApiResponse<OutboundResponse> getOutboundDetail(@PathVariable Long id) {
        return new ApiResponse<>(outboundService.getOutboundDetail(id));
    }
    @GetMapping("/{id}/export")
    @PreAuthorize("hasAuthority('all') or hasAuthority('stock_read')")
    public ResponseEntity<byte[]> exportPdf(@PathVariable Long id) {
        byte[] pdfBytes = outboundService.exportOutboundDetail(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "Phieu_Xuat_Kho_" + id + ".pdf");
        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }
}