package com.gsvn.inventoryservice.controller;

import com.gsvn.inventoryservice.common.ApiResponse;
import com.gsvn.inventoryservice.common.PageResponse;
import com.gsvn.inventoryservice.model.dto.request.InboundRequest;
import com.gsvn.inventoryservice.model.dto.response.InboundResponse;
import com.gsvn.inventoryservice.service.InboundService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inbound")
@RequiredArgsConstructor
public class InboundController {

    private final InboundService inboundService;
    @PostMapping
    @PreAuthorize("hasAuthority('all') or hasAuthority('inbound_create')")
    public ApiResponse<InboundResponse> createInbound(@RequestBody @Valid InboundRequest request) {
        return new ApiResponse<>(inboundService.processInbound(request));
    }
    @GetMapping
    @PreAuthorize("hasAuthority('all') or hasAuthority('inbound_read')")
    public ApiResponse<PageResponse<InboundResponse>> getInboundPage(
            @RequestParam(required = false) Integer warehouseId,
            @RequestParam(required = false) Integer supplierId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return new ApiResponse<>(inboundService.getInboundPage(warehouseId,supplierId, type, keyword, page, size));
    }
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('all') or hasAuthority('inbound_read')")
    public ApiResponse<InboundResponse> getInboundDetail(@PathVariable Long id) {
        return new ApiResponse<>(inboundService.getInboundDetail(id));
    }
    @GetMapping("/{id}/export")
    @PreAuthorize("hasAuthority('all') or hasAuthority('inbound_read')")
    public ResponseEntity<byte[]> exportPdf(@PathVariable Long id) {
        byte[] pdfBytes = inboundService.exportInboundDetail(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "Phieu_Nhap_Kho_" + id + ".pdf");
        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }

}