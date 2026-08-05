package com.gsvn.inventoryservice.controller;

import com.gsvn.inventoryservice.common.ApiResponse;
import com.gsvn.inventoryservice.common.PageResponse;
import com.gsvn.inventoryservice.model.dto.request.OutboundRequest;
import com.gsvn.inventoryservice.model.dto.response.OutboundResponse;
import com.gsvn.inventoryservice.service.OutboundService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Outbound Management", description = "Endpoints for creating, managing, searching, and exporting stock outbound receipts")
public class OutboundController {

    private final OutboundService outboundService;

    @Operation(summary = "Create stock outbound receipt", description = "Processes and creates a new stock outbound receipt for orders, transfers, or inventory adjustments.")
    @PostMapping
    @PreAuthorize("hasAuthority('all') or hasAuthority('outbound_create')")
    public ApiResponse<OutboundResponse> createOutbound(@RequestBody @Valid OutboundRequest request) {
        return new ApiResponse<>(outboundService.processOutbound(request));
    }

    @Operation(summary = "Search stock outbound receipts", description = "Retrieves a paginated list of outbound receipts filtered by warehouse, transaction type, or keyword.")
    @GetMapping
    @PreAuthorize("hasAuthority('all') or hasAuthority('stock_read')")
    public ApiResponse<PageResponse<OutboundResponse>> getOutboundPage(
            @Parameter(description = "Filter by source warehouse ID") @RequestParam(required = false) Integer warehouseId,
            @Parameter(description = "Filter by outbound type (e.g., SALE, TRANSFER, DISPOSAL)") @RequestParam(required = false) String type,
            @Parameter(description = "Keyword to search by receipt code or note") @RequestParam(required = false) String keyword,
            @Parameter(description = "Page number (1-based index)") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Number of records per page") @RequestParam(defaultValue = "10") int size
    ) {
        return new ApiResponse<>(outboundService.getOutboundPage(warehouseId, type, keyword, page, size));
    }

    @Operation(summary = "Get outbound receipt detail", description = "Retrieves detailed information of a specific stock outbound receipt by ID.")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('all') or hasAuthority('stock_read')")
    public ApiResponse<OutboundResponse> getOutboundDetail(
            @Parameter(description = "ID of the outbound receipt") @PathVariable Long id) {
        return new ApiResponse<>(outboundService.getOutboundDetail(id));
    }

    @Operation(summary = "Export outbound receipt PDF", description = "Generates and downloads a PDF document for a specific stock outbound receipt.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Outbound receipt PDF generated successfully",
            content = @Content(mediaType = MediaType.APPLICATION_PDF_VALUE, schema = @Schema(type = "string", format = "binary"))
    )
    @GetMapping("/{id}/export")
    @PreAuthorize("hasAuthority('all') or hasAuthority('stock_read')")
    public ResponseEntity<byte[]> exportPdf(
            @Parameter(description = "ID of the outbound receipt to export") @PathVariable Long id) {
        byte[] pdfBytes = outboundService.exportOutboundDetail(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "Phieu_Xuat_Kho_" + id + ".pdf");
        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }
}