package com.gsvn.promotionservice.controller;

import com.gsvn.promotionservice.common.ApiResponse;
import com.gsvn.promotionservice.common.PageResponse;
import com.gsvn.promotionservice.model.dto.request.VoucherRequest;

import com.gsvn.promotionservice.model.dto.response.VoucherResponse;
import com.gsvn.promotionservice.service.VoucherService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/vouchers")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VoucherController {
    VoucherService voucherService;

    @PostMapping
    public ApiResponse<VoucherResponse> create(@RequestBody @Valid VoucherRequest request) {
        return new ApiResponse<>(voucherService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<VoucherResponse> update(
            @PathVariable Integer id,
            @RequestBody @Valid VoucherRequest request) {
        return new ApiResponse<>(voucherService.update(id, request));
    }

    @GetMapping("/{id}")
    public ApiResponse<VoucherResponse> getById(@PathVariable Integer id) {
        return new ApiResponse<>(voucherService.getById(id));
    }

    @GetMapping("/{code}/byCode")
    public ApiResponse<VoucherResponse> getById(@PathVariable String code) {
        return new ApiResponse<>(voucherService.getByCode(code));
    }

    @GetMapping("/search")
    public ApiResponse<PageResponse<VoucherResponse>> getPage(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return new ApiResponse<>(voucherService.getPage(keyword, page,size,sortBy,direction));
    }
}