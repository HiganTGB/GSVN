package com.gsvn.inventoryservice.controller;

import com.gsvn.inventoryservice.common.ApiResponse;
import com.gsvn.inventoryservice.common.PageResponse;
import com.gsvn.inventoryservice.model.dto.request.InboundRequest;
import com.gsvn.inventoryservice.model.dto.response.InboundResponse;
import com.gsvn.inventoryservice.service.InboundService;

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
@RequestMapping("/api/v1/inbound")
@RequiredArgsConstructor
@Tag(name = "Inbound Management", description = "Endpoints for creating, managing, searching, and exporting stock inbound receipts")
public class InboundController {

    private final InboundService inboundService;

    @Operation(summary = "Create stock inbound receipt", description = "Processes and creates a new stock inbound receipt for inventory replenishment or returns.")
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('inbound_create'))")
    public ApiResponse<InboundResponse> createInbound(@RequestBody @Valid InboundRequest request) {
        return new ApiResponse<>(inboundService.processInbound(request));
    }

    @Operation(summary = "Search stock inbound receipts", description = "Retrieves a paginated list of inbound receipts filtered by warehouse, supplier, transaction type, or keyword.")
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('inbound_read'))")
    public ApiResponse<PageResponse<InboundResponse>> getInboundPage(
            @Parameter(description = "Filter by destination warehouse ID") @RequestParam(required = false) Integer warehouseId,
            @Parameter(description = "Filter by supplier ID") @RequestParam(required = false) Integer supplierId,
            @Parameter(description = "Filter by inbound type (e.g., PURCHASE, RETURN, ADJUSTMENT)") @RequestParam(required = false) String type,
            @Parameter(description = "Keyword to search by receipt code or note") @RequestParam(required = false) String keyword,
            @Parameter(description = "Page number (1-based index)") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Number of records per page") @RequestParam(defaultValue = "10") int size
    ) {
        return new ApiResponse<>(inboundService.getInboundPage(warehouseId, supplierId, type, keyword, page, size));
    }

    @Operation(summary = "Get inbound receipt detail", description = "Retrieves detailed information of a specific stock inbound receipt by ID.")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('inbound_read'))")
    public ApiResponse<InboundResponse> getInboundDetail(
            @Parameter(description = "ID of the inbound receipt") @PathVariable Long id) {
        return new ApiResponse<>(inboundService.getInboundDetail(id));
    }

    @Operation(summary = "Export inbound receipt PDF", description = "Generates and downloads a PDF document for a specific stock inbound receipt.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Inbound receipt PDF generated successfully",
            content = @Content(mediaType = MediaType.APPLICATION_PDF_VALUE, schema = @Schema(type = "string", format = "binary"))
    )
    @GetMapping("/{id}/export")
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('inbound_read'))")
    public ResponseEntity<byte[]> exportPdf(
            @Parameter(description = "ID of the inbound receipt to export") @PathVariable Long id) {
        byte[] pdfBytes = inboundService.exportInboundDetail(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "Phieu_Nhap_Kho_" + id + ".pdf");
        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }
}