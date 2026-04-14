package com.gsvn.accountservice.controller;

import com.gsvn.accountservice.common.ApiResponse;
import com.gsvn.accountservice.model.dto.request.PermissionRequest;
import com.gsvn.accountservice.model.dto.response.PermissionResponse;
import com.gsvn.accountservice.service.PermissionService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PermissionController {
    PermissionService permissionService;

    @PostMapping
    ApiResponse<PermissionResponse> create(@RequestBody @Valid PermissionRequest request) {
        return new ApiResponse<>( permissionService.create(request));
    }

    @GetMapping
    ApiResponse<List<PermissionResponse>> getAll() {
        return new ApiResponse<>( permissionService.getAll());
    }

    @DeleteMapping("/{permissionId}")
    ApiResponse<Void> delete(@PathVariable Integer permissionId) {
        permissionService.delete(permissionId);
        return new ApiResponse<Void>();
    }
}