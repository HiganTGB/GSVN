package com.gsvn.promotionservice.controller;

import com.gsvn.promotionservice.common.ApiResponse;
import com.gsvn.promotionservice.common.PageResponse;
import com.gsvn.promotionservice.model.dto.request.VoucherRequest;
import com.gsvn.promotionservice.model.dto.response.VoucherResponse;
import com.gsvn.promotionservice.service.VoucherService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/vouchers")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Voucher & Promotion Management", description = "Endpoints for creating, updating, searching, and validating promotional discount vouchers")
public class VoucherController {

    VoucherService voucherService;

    @Operation(summary = "Create voucher", description = "Creates a new promotional discount voucher campaign.")
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('voucher_create'))")
    public ApiResponse<VoucherResponse> create(@RequestBody @Valid VoucherRequest request) {
        return new ApiResponse<>(voucherService.create(request));
    }

    @Operation(summary = "Update voucher", description = "Updates details and terms of an existing discount voucher by ID.")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('voucher_update'))")
    public ApiResponse<VoucherResponse> update(
            @Parameter(description = "ID of the voucher") @PathVariable Integer id,
            @RequestBody @Valid VoucherRequest request) {
        return new ApiResponse<>(voucherService.update(id, request));
    }

    @Operation(summary = "Get voucher by ID", description = "Retrieves detailed information of a specific voucher by ID.")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_STAFF')")
    public ApiResponse<VoucherResponse> getById(
            @Parameter(description = "ID of the voucher") @PathVariable Integer id) {
        return new ApiResponse<>(voucherService.getById(id));
    }

    @Operation(summary = "Get voucher by code", description = "Validates and retrieves voucher details by its unique promotional promo code.")
    @GetMapping("/{code}/byCode")
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_STAFF')")
    public ApiResponse<VoucherResponse> getById(
            @Parameter(description = "Unique promotional voucher code (e.g., SUMMER2026)") @PathVariable String code) {
        return new ApiResponse<>(voucherService.getByCode(code));
    }

    @Operation(summary = "Search vouchers with pagination", description = "Retrieves a paginated list of vouchers filtered by keyword with dynamic sorting.")
    @GetMapping("/search")
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('voucher_read'))")
    public ApiResponse<PageResponse<VoucherResponse>> getPage(
            @Parameter(description = "Keyword to search vouchers by name, code, or description")
            @RequestParam(required = false) String keyword,

            @Parameter(description = "Field name to sort results by")
            @RequestParam(defaultValue = "id") String sortBy,

            @Parameter(description = "Sorting direction: 'asc' or 'desc'")
            @RequestParam(defaultValue = "desc") String direction,

            @Parameter(description = "Page number (1-based index)")
            @RequestParam(defaultValue = "1") int page,

            @Parameter(description = "Number of items per page")
            @RequestParam(defaultValue = "10") int size) {
        return new ApiResponse<>(voucherService.getPage(keyword, page, size, sortBy, direction));
    }
}